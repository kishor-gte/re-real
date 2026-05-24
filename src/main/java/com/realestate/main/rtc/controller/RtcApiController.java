package com.realestate.main.rtc.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.realestate.main.dto.response.ApiResponse;
import com.realestate.main.rtc.dto.ChatMessageDto;
import com.realestate.main.rtc.dto.ChatRoomSessionDto;
import com.realestate.main.rtc.dto.ChatSendPayload;
import com.realestate.main.rtc.dto.ChatUnreadSummaryDto;
import com.realestate.main.rtc.dto.OpenChatRequest;
import com.realestate.main.rtc.dto.RtcPrincipal;
import com.realestate.main.rtc.enums.PresenceStatus;
import com.realestate.main.rtc.enums.RtcParticipantType;
import com.realestate.main.rtc.service.ChatRoomService;
import com.realestate.main.rtc.service.ChatService;
import com.realestate.main.rtc.service.PresenceService;
import com.realestate.main.rtc.service.RtcAuthService;
import com.realestate.main.rtc.service.VoiceCallService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/rtc")
public class RtcApiController {

	private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "gif", "webp", "pdf", "mp3", "wav",
			"ogg", "m4a");

	private final RtcAuthService rtcAuthService;
	private final ChatRoomService chatRoomService;
	private final ChatService chatService;
	private final PresenceService presenceService;
	private final VoiceCallService voiceCallService;

	@Value("${app.upload.rtc-dir:uploads/rtc}")
	private String rtcUploadDir;

	public RtcApiController(RtcAuthService rtcAuthService, ChatRoomService chatRoomService, ChatService chatService,
			PresenceService presenceService, VoiceCallService voiceCallService) {
		this.rtcAuthService = rtcAuthService;
		this.chatRoomService = chatRoomService;
		this.chatService = chatService;
		this.presenceService = presenceService;
		this.voiceCallService = voiceCallService;
	}

	@PostMapping("/chat/open")
	public ResponseEntity<ApiResponse<ChatRoomSessionDto>> openChat(@RequestBody OpenChatRequest request,
			HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("Chat ready", chatRoomService.openRoom(request, me)));
	}

	@PostMapping("/chat/send")
	public ResponseEntity<ApiResponse<ChatMessageDto>> sendChat(@RequestBody ChatSendPayload payload,
			HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("Sent", chatService.send(payload, me)));
	}

	@GetMapping("/chat/rooms/{roomId}/unread")
	public ResponseEntity<ApiResponse<Long>> unread(@PathVariable Long roomId, HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", chatService.unreadCount(roomId, me)));
	}

	@GetMapping("/chat/unread-summary")
	public ResponseEntity<ApiResponse<ChatUnreadSummaryDto>> unreadSummary(HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", chatService.unreadSummary(me)));
	}

	@DeleteMapping("/chat/messages/{messageId}")
	public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long messageId, HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		chatService.deleteMessage(messageId, me);
		return ResponseEntity.ok(ApiResponse.ok("Message deleted", null));
	}

	@PostMapping("/chat/rooms/{roomId}/upload")
	public ResponseEntity<ApiResponse<ChatMessageDto>> upload(@PathVariable Long roomId,
			@RequestParam("file") MultipartFile file, @RequestParam(value = "message", required = false) String message,
			@RequestParam(value = "replyToMessageId", required = false) Long replyToMessageId,
			HttpSession session) throws IOException {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().body(ApiResponse.fail("File is required"));
		}
		String ext = extension(file.getOriginalFilename());
		if (!ALLOWED_EXT.contains(ext)) {
			return ResponseEntity.badRequest().body(ApiResponse.fail("File type not allowed"));
		}
		if (file.getSize() > 10 * 1024 * 1024) {
			return ResponseEntity.badRequest().body(ApiResponse.fail("Max file size is 10MB"));
		}
		Path dir = Paths.get(rtcUploadDir).toAbsolutePath().normalize();
		Files.createDirectories(dir);
		String stored = UUID.randomUUID() + "." + ext;
		Path target = dir.resolve(stored);
		Files.copy(file.getInputStream(), target);
		String url = "/uploads/rtc/" + stored;
		String type = file.getContentType() != null ? file.getContentType() : ext;
		ChatMessageDto dto = chatService.sendWithAttachment(roomId, me, message, url, type, replyToMessageId);
		return ResponseEntity.ok(ApiResponse.ok("Uploaded", dto));
	}

	@GetMapping("/presence")
	public ResponseEntity<ApiResponse<Map<String, String>>> presence(@RequestParam String type,
			@RequestParam Long id) {
		RtcParticipantType pt = RtcParticipantType.valueOf(type.toUpperCase());
		PresenceStatus status = presenceService.getStatus(pt, id);
		return ResponseEntity.ok(ApiResponse.ok("OK", Map.of("status", status.name())));
	}

	@GetMapping("/calls/history")
	public ResponseEntity<ApiResponse<List<Map<String, Object>>>> callHistory(HttpSession session) {
		RtcPrincipal me = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", voiceCallService.historyForPrincipal(me)));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<Map<String, Object>>> me(HttpSession session) {
		RtcPrincipal p = rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", Map.of(
				"type", p.getType().name(),
				"id", p.getId(),
				"displayName", p.getDisplayName() != null ? p.getDisplayName() : "",
				"destinationUser", p.destinationUser())));
	}

	@GetMapping("/config")
	public ResponseEntity<ApiResponse<Map<String, Object>>> config(HttpSession session) {
		rtcAuthService.requirePrincipal(session);
		return ResponseEntity.ok(ApiResponse.ok("OK", Map.of("wsEndpoint", "/ws/rtc")));
	}

	private String extension(String name) {
		if (name == null || !name.contains(".")) {
			return "";
		}
		return name.substring(name.lastIndexOf('.') + 1).toLowerCase();
	}
}
