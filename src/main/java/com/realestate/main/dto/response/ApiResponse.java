package com.realestate.main.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

public class ApiResponse<T> {

	private boolean success;
	private String message;
	private T data;
	private LocalDateTime timestamp = LocalDateTime.now();

	public ApiResponse() {
	}

	public ApiResponse(boolean success, String message, T data, LocalDateTime timestamp) {
		this.success = success;
		this.message = message;
		this.data = data;
		this.timestamp = timestamp;
	}

	public static <T> ApiResponse<T> ok(String message, T data) {
		ApiResponse<T> r = new ApiResponse<>();
		r.setSuccess(true);
		r.setMessage(message);
		r.setData(data);
		r.setTimestamp(LocalDateTime.now());
		return r;
	}

	public static <T> ApiResponse<T> ok(String message) {
		return ok(message, null);
	}

	public static ApiResponse<Map<String, String>> fail(String message, Map<String, String> errors) {
		ApiResponse<Map<String, String>> r = new ApiResponse<>();
		r.setSuccess(false);
		r.setMessage(message);
		r.setData(errors);
		r.setTimestamp(LocalDateTime.now());
		return r;
	}

	public static <T> ApiResponse<T> fail(String message) {
		ApiResponse<T> r = new ApiResponse<>();
		r.setSuccess(false);
		r.setMessage(message);
		r.setTimestamp(LocalDateTime.now());
		return r;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}
