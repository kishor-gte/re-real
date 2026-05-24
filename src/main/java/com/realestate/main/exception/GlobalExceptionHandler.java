package com.realestate.main.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.persistence.PersistenceException;

import com.realestate.main.dto.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(AuthException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuth(AuthException ex) {
		return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
	}

	@ExceptionHandler(EmailSendException.class)
	public ResponseEntity<ApiResponse<Void>> handleEmail(EmailSendException ex) {
		return ResponseEntity.badRequest().body(ApiResponse.fail(ex.getMessage()));
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiResponse<Void>> handleIntegrity(DataIntegrityViolationException ex) {
		log.error("Database integrity violation", ex);
		String msg = rootMessage(ex);
		if (msg.contains("foreign key") || msg.contains("cannot delete") || msg.contains("1451")) {
			return ResponseEntity.badRequest()
					.body(ApiResponse.fail("Could not reset a prior application. Please contact support."));
		}
		if (msg.contains("pg_properties") || msg.contains("pg_code")) {
			return ResponseEntity.badRequest()
					.body(ApiResponse.fail("Could not save PG property. Please try again."));
		}
		if (msg.contains("pg_owners") && (msg.contains("email") || msg.contains("mobile"))) {
			return ResponseEntity.badRequest()
					.body(ApiResponse.fail("Email or mobile is already registered. Please sign in."));
		}
		if (msg.contains("duplicate") || msg.contains("unique")) {
			if (msg.contains("pg_") || msg.contains("pg")) {
				return ResponseEntity.badRequest()
						.body(ApiResponse.fail("Could not save PG listing. Please try again."));
			}
			return ResponseEntity.badRequest()
					.body(ApiResponse.fail("This record already exists. Please refresh and try again."));
		}
		return ResponseEntity.badRequest().body(ApiResponse.fail("Database error. Please try again."));
	}

	@ExceptionHandler(PersistenceException.class)
	public ResponseEntity<ApiResponse<Void>> handlePersistence(PersistenceException ex) {
		log.error("Persistence error", ex);
		String msg = ex.getMessage() != null && ex.getMessage().contains("null identifier")
				? "Registration could not be completed. Please try again."
				: "Database error. Please try again.";
		return ResponseEntity.badRequest().body(ApiResponse.fail(msg));
	}

	private String rootMessage(Throwable ex) {
		Throwable t = ex;
		while (t.getCause() != null) {
			t = t.getCause();
		}
		return t.getMessage() != null ? t.getMessage().toLowerCase() : "";
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse<Void>> handleMaxUpload(MaxUploadSizeExceededException ex) {
		return ResponseEntity.badRequest()
				.body(ApiResponse.fail("Uploaded file is too large. Maximum allowed size is 20 MB per file."));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleBind(BindException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed", errors));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
		Map<String, String> errors = new HashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.put(error.getField(), error.getDefaultMessage());
		}
		return ResponseEntity.badRequest().body(ApiResponse.fail("Validation failed", errors));
	}
}
