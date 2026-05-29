package com.realestate.main.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import java.time.Duration;

import com.realestate.main.exception.AuthException;

@Service
public class FileStorageService {

	private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

	private static final Set<String> GOVERNMENT_ID_EXTENSIONS = Set.of(".pdf", ".doc", ".docx", ".ppt", ".pptx", ".xls",
			".xlsx", ".txt", ".rtf", ".odt", ".ods", ".odp", ".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp", ".heic");

	@Value("${app.upload.dir:uploads/profiles}")
	private String uploadDir;

	@Value("${app.upload.admin-dir:uploads/admin-profiles}")
	private String adminUploadDir;

	@Value("${app.upload.agent-dir:uploads/agent}")
	private String agentUploadDir;

	@Value("${app.upload.pg-owner-dir:uploads/pg-owner}")
	private String pgOwnerUploadDir;

	@Value("${app.upload.property-dir:uploads/properties}")
	private String propertyUploadDir;

	@Value("${app.upload.agent-document-max-mb:20}")
	private long agentDocumentMaxMb;

	@Value("${app.upload.property-image-max-mb:10}")
	private long propertyImageMaxMb;

	@Value("${app.upload.pg-property-image-max-mb:5}")
	private long pgPropertyImageMaxMb;

	@Value("${app.upload.pg-property-max-images:30}")
	private int pgPropertyMaxImages;

	@Value("${app.upload.rtc-dir:uploads/rtc}")
	private String rtcUploadDir;

	@Value("${APP_STORAGE_PROVIDER:local}")
	private String storageProvider;

	@Value("${APP_S3_BUCKET:}")
	private String s3Bucket;

	@Value("${APP_S3_REGION:}")
	private String s3Region;

	private S3Client s3Client;
	private S3Presigner s3Presigner;

	private static final Set<String> PROPERTY_IMAGE_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp",
			".bmp", ".heic");

	public String storeProfileImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		try {
			if ("s3".equalsIgnoreCase(storageProvider)) {
				String ext = getExtension(file.getOriginalFilename());
				String key = "profiles/" + UUID.randomUUID() + ext;
				try (InputStream in = file.getInputStream()) {
					String url = uploadToS3(in, key, file.getContentType(), file.getSize());
					if (url != null) return url;
				}
			}
			Path dir = Paths.get(uploadDir);
			Files.createDirectories(dir);
			String ext = getExtension(file.getOriginalFilename());
			String filename = UUID.randomUUID() + ext;
			Path target = dir.resolve(filename);
			file.transferTo(target.toFile());
			return "/uploads/profiles/" + filename;
		} catch (IOException e) {
			return null;
		}
	}

	public String storeAdminProfileImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		try {
			if ("s3".equalsIgnoreCase(storageProvider)) {
				String ext = getExtension(file.getOriginalFilename());
				String key = "admin-profiles/" + UUID.randomUUID() + ext;
				try (InputStream in = file.getInputStream()) {
					String url = uploadToS3(in, key, file.getContentType(), file.getSize());
					if (url != null) return url;
				}
			}
			Path dir = Paths.get(adminUploadDir);
			Files.createDirectories(dir);
			String ext = getExtension(file.getOriginalFilename());
			String filename = UUID.randomUUID() + ext;
			Path target = dir.resolve(filename);
			file.transferTo(target.toFile());
			return "/uploads/admin-profiles/" + filename;
		} catch (IOException e) {
			return null;
		}
	}

	public String storeAgentProfilePhoto(MultipartFile file) {
		return storeAgentFile(file, "profiles");
	}

	public String storeAgentAgencyLogo(MultipartFile file) {
		return storeAgentFile(file, "logos");
	}

	public String storePropertyImage(MultipartFile file, Long agentId) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String ext = getExtensionLower(file.getOriginalFilename());
		if (ext.isEmpty() || !PROPERTY_IMAGE_EXTENSIONS.contains(ext)) {
			throw new AuthException("Property image must be JPG, PNG, GIF, or WebP");
		}
		long maxBytes = propertyImageMaxMb * 1024L * 1024L;
		if (file.getSize() > maxBytes) {
			throw new AuthException("Property image is too large. Maximum size is " + propertyImageMaxMb + " MB.");
		}
		try {
			if ("s3".equalsIgnoreCase(storageProvider)) {
				String filename = UUID.randomUUID() + ext;
				String key = "properties/" + agentId + "/" + filename;
				try (InputStream in = file.getInputStream()) {
					String url = uploadToS3(in, key, file.getContentType(), file.getSize());
					if (url != null) return url;
				}
			}
			Path dir = Paths.get(propertyUploadDir, String.valueOf(agentId)).toAbsolutePath().normalize();
			Files.createDirectories(dir);
			String filename = UUID.randomUUID() + ext;
			Path target = dir.resolve(filename);
			try (InputStream in = file.getInputStream()) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
			log.info("Stored property image: {}", target);
			return "/uploads/properties/" + agentId + "/" + filename;
		} catch (IOException e) {
			log.error("Failed to store property image: {}", e.getMessage(), e);
			throw new AuthException("Could not upload property image. Please try again.");
		}
	}

	public String storeAgentGovernmentId(MultipartFile file) {
		validateGovernmentIdUpload(file);
		String stored = storeAgentFile(file, "documents", true);
		if (stored == null) {
			throw new AuthException("Could not upload government ID. Please try again.");
		}
		return stored;
	}

	public String storePgOwnerProfilePhoto(MultipartFile file) {
		return storePgOwnerFile(file, "profiles");
	}

	public String storePgOwnerGovernmentId(MultipartFile file) {
		validateGovernmentIdUpload(file);
		String stored = storePgOwnerFile(file, "documents", true);
		if (stored == null) {
			throw new AuthException("Could not upload government ID. Please try again.");
		}
		return stored;
	}

	public String storePgPropertyImage(MultipartFile file, Long ownerId, Long pgPropertyId) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		String ext = getExtensionLower(file.getOriginalFilename());
		if (ext.isEmpty() || !PROPERTY_IMAGE_EXTENSIONS.contains(ext)) {
			throw new AuthException("PG property image must be JPG, PNG, or WebP");
		}
		long maxBytes = pgPropertyImageMaxMb * 1024L * 1024L;
		if (file.getSize() > maxBytes) {
			throw new AuthException("PG property image is too large. Maximum size is " + pgPropertyImageMaxMb + " MB.");
		}
		String subFolder = ownerId + "/properties/" + pgPropertyId;
		return storePgOwnerFile(file, subFolder, true);
	}

	public void validatePgPropertyImageBatch(List<MultipartFile> files, boolean requireMinimum) {
		if (files == null) {
			files = List.of();
		}
		List<MultipartFile> valid = files.stream().filter(f -> f != null && !f.isEmpty()).toList();
		if (requireMinimum && valid.size() < 3) {
			throw new AuthException("Please upload at least 3 PG property images");
		}
		if (valid.size() > pgPropertyMaxImages) {
			throw new AuthException("Maximum " + pgPropertyMaxImages + " images allowed per PG property");
		}
		for (MultipartFile file : valid) {
			String ext = getExtensionLower(file.getOriginalFilename());
			if (ext.isEmpty() || !PROPERTY_IMAGE_EXTENSIONS.contains(ext)) {
				throw new AuthException("PG property images must be JPG, PNG, or WebP");
			}
			long maxBytes = pgPropertyImageMaxMb * 1024L * 1024L;
			if (file.getSize() > maxBytes) {
				throw new AuthException("Each PG property image must be under " + pgPropertyImageMaxMb + " MB");
			}
		}
	}

	public String storePgOwnerPgImages(MultipartFile[] files) {
		if (files == null || files.length == 0) {
			throw new AuthException("At least one PG image is required");
		}
		List<String> paths = new ArrayList<>();
		for (MultipartFile file : files) {
			if (file == null || file.isEmpty()) {
				continue;
			}
			String ext = getExtensionLower(file.getOriginalFilename());
			if (ext.isEmpty() || !PROPERTY_IMAGE_EXTENSIONS.contains(ext)) {
				throw new AuthException("PG image must be JPG, PNG, GIF, or WebP");
			}
			long maxBytes = propertyImageMaxMb * 1024L * 1024L;
			if (file.getSize() > maxBytes) {
				throw new AuthException("PG image is too large. Maximum size is " + propertyImageMaxMb + " MB.");
			}
			String stored = storePgOwnerFile(file, "pg-images", true);
			if (stored != null) {
				paths.add(stored);
			}
		}
		if (paths.isEmpty()) {
			throw new AuthException("At least one PG image is required");
		}
		try {
			return new ObjectMapper().writeValueAsString(paths);
		} catch (Exception e) {
			throw new AuthException("Could not save PG images. Please try again.");
		}
	}

	public String storeRtcFile(MultipartFile file) {
		if (file == null || file.isEmpty()) return null;
		String ext = getExtension(file.getOriginalFilename());
		String filename = UUID.randomUUID() + ext;
		if ("s3".equalsIgnoreCase(storageProvider)) {
			String key = "rtc/" + filename;
			try (InputStream in = file.getInputStream()) {
				String url = uploadToS3(in, key, file.getContentType(), file.getSize());
				if (url != null) return url;
			} catch (IOException e) {
				log.warn("RTC upload to S3 failed: {}", e.getMessage());
			}
		}
		try {
			Path dir = Paths.get(rtcUploadDir).toAbsolutePath().normalize();
			Files.createDirectories(dir);
			Path target = dir.resolve(filename);
			try (InputStream in = file.getInputStream()) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
			return "/uploads/rtc/" + filename;
		} catch (IOException e) {
			log.error("Failed to store RTC file: {}", e.getMessage(), e);
			return null;
		}
	}

	private void validateGovernmentIdUpload(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new AuthException("Government ID proof is required");
		}
		String original = file.getOriginalFilename();
		String ext = getExtensionLower(original);
		if (ext.isEmpty() || !GOVERNMENT_ID_EXTENSIONS.contains(ext)) {
			throw new AuthException(
					"Unsupported file type. Allowed: PDF, Word (.doc/.docx), Excel, PowerPoint (.ppt/.pptx), and images.");
		}
		long maxBytes = agentDocumentMaxMb * 1024L * 1024L;
		if (file.getSize() > maxBytes) {
			throw new AuthException("Government ID file is too large. Maximum size is " + agentDocumentMaxMb + " MB.");
		}
	}

	private String storeAgentFile(MultipartFile file, String subFolder, boolean failOnError) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		try {
			String ext = getExtensionLower(file.getOriginalFilename());
			if (ext.isEmpty()) {
				ext = ".bin";
			}
			String filename = UUID.randomUUID() + ext;
			if ("s3".equalsIgnoreCase(storageProvider)) {
				String key = "agent/" + subFolder + "/" + filename;
				try (InputStream in = file.getInputStream()) {
					String url = uploadToS3(in, key, file.getContentType(), file.getSize());
					if (url != null) return url;
				}
			}
			Path dir = Paths.get(agentUploadDir, subFolder).toAbsolutePath().normalize();
			Files.createDirectories(dir);
			Path target = dir.resolve(filename);
			try (InputStream in = file.getInputStream()) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
			log.info("Stored agent file: {}", target);
			return "/uploads/agent/" + subFolder + "/" + filename;
		} catch (IOException e) {
			log.error("Failed to store agent file in {}: {}", subFolder, e.getMessage(), e);
			if (failOnError) {
				throw new AuthException("Could not save uploaded file. Please try again.");
			}
			return null;
		}
	}

	private String storeAgentFile(MultipartFile file, String subFolder) {
		return storeAgentFile(file, subFolder, false);
	}

	private String storePgOwnerFile(MultipartFile file, String subFolder, boolean failOnError) {
		if (file == null || file.isEmpty()) {
			return null;
		}
		try {
			String ext = getExtensionLower(file.getOriginalFilename());
			if (ext.isEmpty()) {
				ext = ".bin";
			}
			String filename = UUID.randomUUID() + ext;
			if ("s3".equalsIgnoreCase(storageProvider)) {
				String key = "pg-owner/" + subFolder + "/" + filename;
				try (InputStream in = file.getInputStream()) {
					String url = uploadToS3(in, key, file.getContentType(), file.getSize());
					if (url != null) return url;
				}
			}
			Path dir = Paths.get(pgOwnerUploadDir, subFolder).toAbsolutePath().normalize();
			Files.createDirectories(dir);
			Path target = dir.resolve(filename);
			try (InputStream in = file.getInputStream()) {
				Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
			}
			log.info("Stored PG owner file: {}", target);
			return "/uploads/pg-owner/" + subFolder + "/" + filename;
		} catch (IOException e) {
			log.error("Failed to store PG owner file in {}: {}", subFolder, e.getMessage(), e);
			if (failOnError) {
				throw new AuthException("Could not save uploaded file. Please try again.");
			}
			return null;
		}
	}

	private String storePgOwnerFile(MultipartFile file, String subFolder) {
		return storePgOwnerFile(file, subFolder, false);
	}

	private String getExtension(String name) {
		String ext = getExtensionLower(name);
		if (ext.isEmpty()) {
			return ".jpg";
		}
		return ext;
	}

	private String getExtensionLower(String name) {
		if (name == null || !name.contains(".")) {
			return "";
		}
		return name.substring(name.lastIndexOf('.')).toLowerCase(Locale.ROOT);
	}

	private String uploadToS3(InputStream in, String key, String contentType, long contentLength) {
		S3Client client = s3Client != null ? s3Client : buildS3ClientIfConfigured();
		S3Presigner presigner = s3Presigner != null ? s3Presigner : buildS3PresignerIfConfigured();
		if (client == null) {
			log.warn("S3 client not configured");
			return null;
		}
		try {
			PutObjectRequest req = PutObjectRequest.builder().bucket(s3Bucket).key(key)
				.contentType(contentType == null ? "application/octet-stream" : contentType).build();
			client.putObject(req, RequestBody.fromInputStream(in, contentLength));
			if (presigner != null) {
				// return presigned URL valid for config duration (default 1 hour)
				GetObjectRequest getReq = GetObjectRequest.builder().bucket(s3Bucket).key(key).build();
				GetObjectPresignRequest presignReq = GetObjectPresignRequest.builder()
					.getObjectRequest(getReq)
					.signatureDuration(Duration.ofMinutes(60))
					.build();
				return presigner.presignGetObject(presignReq).url().toString();
			}
			// fallback to public URL
			String url = String.format("https://%s.s3.%s.amazonaws.com/%s", s3Bucket, s3Region, key);
			return url;
		} catch (S3Exception e) {
			log.error("S3 upload failed: {}", e.awsErrorDetails() != null ? e.awsErrorDetails().errorMessage() : e.getMessage());
			return null;
		}
	}

	private S3Presigner buildS3PresignerIfConfigured() {
		if (s3Presigner != null) return s3Presigner;
		if (s3Region == null || s3Region.isBlank()) return null;
		try {
			Region region = Region.of(s3Region);
			s3Presigner = S3Presigner.builder().region(region).build();
			return s3Presigner;
		} catch (Exception e) {
			log.warn("Failed to initialize S3 presigner: {}", e.getMessage());
			return null;
		}
	}

	private S3Client buildS3ClientIfConfigured() {
		if (s3Client != null) return s3Client;
		if (s3Bucket == null || s3Bucket.isBlank() || s3Region == null || s3Region.isBlank()) {
			return null;
		}
		try {
			Region region = Region.of(s3Region);
			s3Client = S3Client.builder().region(region).build();
			return s3Client;
		} catch (Exception e) {
			log.warn("Failed to initialize S3 client: {}", e.getMessage());
			return null;
		}
	}
}
