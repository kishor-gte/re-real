package com.realestate.main.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.realestate.main.config.MailConfig;
import com.realestate.main.dto.response.OtpSendResult;
import com.realestate.main.entity.PgBooking;
import com.realestate.main.entity.PgOwner;
import com.realestate.main.entity.PgProperty;
import com.realestate.main.entity.User;
import com.realestate.main.exception.EmailSendException;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

	private static final Logger log = LoggerFactory.getLogger(EmailService.class);

	private final JavaMailSender mailSender;
	private final ApplicationUrlService applicationUrlService;
	private final ExecutorService mailOtpExecutor;

	@Value("${app.name:EstateVault}")
	private String appName;

	@Value("${app.support-email:support@estatevault.com}")
	private String supportEmail;

	@Value("${spring.mail.username:}")
	private String fromEmail;

	@Value("${spring.mail.password:}")
	private String mailPassword;

	@Value("${spring.mail.host:smtp.gmail.com}")
	private String mailHost;

	@Value("${spring.mail.port:465}")
	private int mailPort;

	/** auto = try SMTP (465 then 587), then console fallback; smtp = SMTP only; console = log OTP only */
	@Value("${app.mail.delivery:auto}")
	private String deliveryMode;

	@Value("${app.mail.fallback-console:true}")
	private boolean fallbackConsole;

	@Value("${app.mail.expose-otp-in-response:true}")
	private boolean exposeOtpInResponse;

	@Value("${app.mail.otp-max-wait-ms:6000}")
	private long otpMaxWaitMs;

	@Value("${app.mail.smtp.connection-timeout-ms:5000}")
	private int smtpConnectionTimeoutMs;

	@Value("${app.mail.smtp.read-timeout-ms:8000}")
	private int smtpReadTimeoutMs;

	public EmailService(JavaMailSender mailSender, ApplicationUrlService applicationUrlService,
			@Qualifier("mailOtpExecutor") ExecutorService mailOtpExecutor) {
		this.mailSender = mailSender;
		this.applicationUrlService = applicationUrlService;
		this.mailOtpExecutor = mailOtpExecutor;
	}

	/** Safe fallback when SMTP throws — keeps agent registration from failing. */
	public OtpSendResult agentOtpFallback(String toEmail, String otp) {
		return consoleResult(normalizeEmail(toEmail), otp);
	}

	/** Safe fallback when SMTP throws — keeps PG owner registration from failing. */
	public OtpSendResult pgOwnerOtpFallback(String toEmail, String otp) {
		return consoleResult(normalizeEmail(toEmail), otp);
	}

	public OtpSendResult sendOtpEmail(String toEmail, String userName, String otp) {
		String to = normalizeEmail(toEmail);
		return sendOtpDelivery(to, otp, appName + " - OTP Verification", buildOtpHtml(userName, to, otp));
	}

	public boolean sendPasswordResetEmail(String toEmail, String userName, String resetUrl, int expiryMinutes) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Reset Your Password",
				"<p>Hello <strong>" + escapeHtml(userName) + "</strong>,</p>"
						+ "<p>We received a request to reset your password. Click the button below (valid for <strong>"
						+ expiryMinutes + " minutes</strong>):</p>"
						+ "<p style='text-align:center;margin:28px 0;'>"
						+ "<a href='" + escapeHtml(resetUrl) + "' style='display:inline-block;padding:14px 28px;"
						+ "background:#D4AF37;color:#0f172a;text-decoration:none;font-weight:700;border-radius:10px;'>"
						+ "Reset Password</a></p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>Or copy this link:<br/>"
						+ "<span style='word-break:break-all;color:#93c5fd;'>" + escapeHtml(resetUrl) + "</span></p>"
						+ "<p>If you did not request this, ignore this email.</p>");
		String subject = appName + " - Password Reset";

		if ("console".equalsIgnoreCase(deliveryMode)) {
			logPasswordResetLink(to, resetUrl);
			return false;
		}

		if (!isMailConfigured()) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Email not configured for password reset.");
		}

		try {
			sendOnPort(to, subject, html, mailPort);
			log.info("Password reset email sent to {}", to);
			return true;
		} catch (Exception e) {
			log.warn("Password reset SMTP failed for {}: {}", to, e.getMessage());
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Could not send password reset email to " + to + ": " + e.getMessage(), e);
		}
	}

	private void logPasswordResetLink(String to, String resetUrl) {
		log.warn("============================================================");
		log.warn("PASSWORD RESET (console) for: {}", to);
		log.warn("RESET LINK: {}", resetUrl);
		log.warn("============================================================");
	}

	public OtpSendResult sendAdminOtpEmail(String toEmail, String adminName, String otp) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Admin Email Verification",
				"<p>Hello <strong>" + escapeHtml(adminName) + "</strong>,</p>"
						+ "<p>Your <strong>admin registration OTP</strong> for <strong>" + escapeHtml(to) + "</strong> is:</p>"
						+ "<p style='font-size:32px;font-weight:bold;color:#60a5fa;letter-spacing:8px;text-align:center;'>"
						+ otp + "</p>"
						+ "<p>This OTP expires in <strong>5 minutes</strong>. Do not share it.</p>"
						+ "<p style='color:#94a3b8;font-size:12px;'>EstateVault Admin Portal — authorized personnel only.</p>");
		return sendOtpDelivery(to, otp, appName + " - Admin OTP Verification", html);
	}

	public boolean sendAdminPasswordResetEmail(String toEmail, String adminName, String resetUrl, int expiryMinutes) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Admin Password Reset",
				"<p>Hello <strong>" + escapeHtml(adminName) + "</strong>,</p>"
						+ "<p>Admin password reset requested. Link valid <strong>" + expiryMinutes + " minutes</strong>:</p>"
						+ "<p style='text-align:center;margin:28px 0;'>"
						+ "<a href='" + escapeHtml(resetUrl) + "' style='display:inline-block;padding:14px 28px;"
						+ "background:#60a5fa;color:#0f172a;text-decoration:none;font-weight:700;border-radius:10px;'>"
						+ "Reset Admin Password</a></p>"
						+ "<p style='font-size:12px;color:#94a3b8;word-break:break-all;'>" + escapeHtml(resetUrl) + "</p>");
		String subject = appName + " - Admin Password Reset";

		if ("console".equalsIgnoreCase(deliveryMode)) {
			logPasswordResetLink(to, resetUrl);
			return false;
		}
		if (!isMailConfigured()) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Email not configured for admin password reset.");
		}
		try {
			sendOnPort(to, subject, html, mailPort);
			return true;
		} catch (Exception e) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Could not send admin reset email: " + e.getMessage(), e);
		}
	}

	public OtpSendResult sendAgentOtpEmail(String toEmail, String agentName, String otp) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Agent Email Verification",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Your <strong>agent registration OTP</strong> for <strong>" + escapeHtml(to) + "</strong> is:</p>"
						+ "<p style='font-size:32px;font-weight:bold;color:#D4AF37;letter-spacing:8px;text-align:center;'>"
						+ otp + "</p>"
						+ "<p>This OTP expires in <strong>5 minutes</strong>. Do not share it with anyone.</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>Support: " + escapeHtml(supportEmail)
						+ " — EstateVault Agent Portal.</p>");
		return sendOtpDeliveryBounded(to, otp, appName + " - Agent OTP Verification", html);
	}

	public boolean sendAgentPasswordResetEmail(String toEmail, String agentName, String resetUrl, int expiryMinutes) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Reset Agent Password",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Reset your agent portal password (valid <strong>" + expiryMinutes + " minutes</strong>):</p>"
						+ "<p style='text-align:center;margin:28px 0;'>"
						+ "<a href='" + escapeHtml(resetUrl) + "' style='display:inline-block;padding:14px 28px;"
						+ "background:#D4AF37;color:#0f172a;text-decoration:none;font-weight:700;border-radius:10px;'>"
						+ "Reset Password</a></p>"
						+ "<p style='font-size:12px;color:#94a3b8;word-break:break-all;'>" + escapeHtml(resetUrl) + "</p>");
		String subject = appName + " - Agent Password Reset";

		if ("console".equalsIgnoreCase(deliveryMode)) {
			logPasswordResetLink(to, resetUrl);
			return false;
		}
		if (!isMailConfigured()) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Email not configured for agent password reset.");
		}
		try {
			sendOnPort(to, subject, html, mailPort);
			return true;
		} catch (Exception e) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Could not send agent reset email: " + e.getMessage(), e);
		}
	}

	@Async
	public void sendAgentPendingApprovalEmail(String toEmail, String agentName, String agentCode) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Application Submitted — Pending Approval",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Your email is verified and your broker application has been submitted successfully.</p>"
						+ "<p>Application reference: <strong style='color:#93c5fd;'>" + escapeHtml(agentCode)
						+ "</strong></p>"
						+ "<p style='color:#eab308;'><strong>Status: On hold — awaiting admin approval</strong></p>"
						+ "<p>You will receive another email once an administrator reviews and approves your account. "
						+ "You cannot sign in until approval is complete.</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>Support: " + escapeHtml(supportEmail) + "</p>");
		sendAsyncSafe(to, appName + " - Agent Application Pending", html);
	}

	@Async
	public void sendAgentApprovedEmail(String toEmail, String agentName, String agentCode) {
		String to = normalizeEmail(toEmail);
		String login = "";
		String base = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (base != null) {
			login = "<p><a href='" + escapeHtml(base + "/agent/login")
					+ "' style='display:inline-block;padding:14px 28px;background:#D4AF37;color:#0f172a;"
					+ "text-decoration:none;font-weight:700;border-radius:10px;'>Sign In to Agent Portal</a></p>";
		}
		String html = wrapTemplate("Agent Account Approved",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Great news! Your EstateVault agent account has been <strong style='color:#22c55e;'>approved</strong>.</p>"
						+ "<p>Agent ID: <strong style='color:#93c5fd;'>" + escapeHtml(agentCode) + "</strong></p>"
						+ login
						+ "<p style='font-size:12px;color:#94a3b8;'>You can now list properties, manage leads, and use your dashboard.</p>");
		sendAsyncSafe(to, appName + " - Agent Account Approved", html);
	}

	@Async
	public void sendAgentRejectedEmail(String toEmail, String agentName, String reason) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Agent Application Update",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>After review, your agent application was <strong style='color:#ef4444;'>not approved</strong> at this time.</p>"
						+ "<p><strong>Reason:</strong> " + escapeHtml(reason) + "</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>For questions, contact " + escapeHtml(supportEmail)
						+ "</p>");
		sendAsyncSafe(to, appName + " - Agent Application Status", html);
	}

	@Async
	public void sendAdminNewAgentAlertEmail(com.realestate.main.entity.Agent agent) {
		if (!isMailConfigured() || fromEmail == null || fromEmail.isBlank()) {
			log.info("New agent pending approval: id={}, email={}, name={}", agent.getId(), agent.getEmail(),
					agent.getFullName());
			return;
		}
		String reviewLink = "";
		String base = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (base != null) {
			reviewLink = "<p><a href='" + escapeHtml(base + "/admin/agents/" + agent.getId())
					+ "' style='color:#60a5fa;font-weight:700;'>Review application in Admin Portal</a></p>";
		}
		String html = wrapTemplate("New Agent Awaiting Approval",
				"<p>A new agent registration requires your review.</p>"
						+ "<p><strong>Name:</strong> " + escapeHtml(agent.getFullName()) + "<br/>"
						+ "<strong>Email:</strong> " + escapeHtml(agent.getEmail()) + "<br/>"
						+ "<strong>Agency:</strong> " + escapeHtml(agent.getAgencyName()) + "<br/>"
						+ "<strong>RERA:</strong> " + escapeHtml(agent.getReraNumber()) + "<br/>"
						+ "<strong>City:</strong> " + escapeHtml(agent.getCity()) + ", " + escapeHtml(agent.getState())
						+ "</p>" + reviewLink);
		sendAsyncSafe(fromEmail, appName + " - New Agent Pending Approval", html);
	}

	public OtpSendResult sendPgOwnerOtpEmail(String toEmail, String ownerName, String otp) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("PG Owner Email Verification",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Your <strong>PG owner registration OTP</strong> for <strong>" + escapeHtml(to)
						+ "</strong> is:</p>"
						+ "<p style='font-size:32px;font-weight:bold;color:#D4AF37;letter-spacing:8px;text-align:center;'>"
						+ otp + "</p>"
						+ "<p>This OTP expires in <strong>5 minutes</strong>. Do not share it with anyone.</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>Support: " + escapeHtml(supportEmail)
						+ " — EstateVault PG Owner Portal.</p>");
		return sendOtpDeliverySync(to, otp, appName + " - PG Owner OTP Verification", html);
	}

	public boolean sendPgOwnerPasswordResetEmail(String toEmail, String ownerName, String resetUrl,
			int expiryMinutes) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Reset PG Owner Password",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Reset your PG owner portal password (valid <strong>" + expiryMinutes
						+ " minutes</strong>):</p>"
						+ "<p style='text-align:center;margin:28px 0;'>"
						+ "<a href='" + escapeHtml(resetUrl) + "' style='display:inline-block;padding:14px 28px;"
						+ "background:#D4AF37;color:#0f172a;text-decoration:none;font-weight:700;border-radius:10px;'>"
						+ "Reset Password</a></p>"
						+ "<p style='font-size:12px;color:#94a3b8;word-break:break-all;'>" + escapeHtml(resetUrl)
						+ "</p>");
		String subject = appName + " - PG Owner Password Reset";

		if ("console".equalsIgnoreCase(deliveryMode)) {
			logPasswordResetLink(to, resetUrl);
			return false;
		}
		if (!isMailConfigured()) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Email not configured for PG owner password reset.");
		}
		try {
			sendOnPort(to, subject, html, mailPort);
			return true;
		} catch (Exception e) {
			if (fallbackConsole) {
				logPasswordResetLink(to, resetUrl);
				return false;
			}
			throw new EmailSendException("Could not send PG owner reset email: " + e.getMessage(), e);
		}
	}

	@Async
	public void sendPgOwnerPendingApprovalEmail(String toEmail, String ownerName, String pgOwnerCode) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("Application Submitted — Pending Approval",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Your email is verified and your PG owner application has been submitted successfully.</p>"
						+ "<p>Application reference: <strong style='color:#93c5fd;'>" + escapeHtml(pgOwnerCode)
						+ "</strong></p>"
						+ "<p style='color:#eab308;'><strong>Status: On hold — awaiting admin approval</strong></p>"
						+ "<p>You will receive another email once an administrator reviews and approves your account. "
						+ "You cannot sign in until approval is complete.</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>Support: " + escapeHtml(supportEmail) + "</p>");
		sendAsyncSafe(to, appName + " - PG Owner Application Pending", html);
	}

	@Async
	public void sendPgOwnerApprovedEmail(String toEmail, String ownerName, String pgOwnerCode) {
		String to = normalizeEmail(toEmail);
		String login = "";
		String base = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (base != null) {
			login = "<p><a href='" + escapeHtml(base + "/pg-owner/login")
					+ "' style='display:inline-block;padding:14px 28px;background:#D4AF37;color:#0f172a;"
					+ "text-decoration:none;font-weight:700;border-radius:10px;'>Sign In to PG Owner Portal</a></p>";
		}
		String html = wrapTemplate("PG Owner Account Approved",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Great news! Your EstateVault PG owner account has been <strong style='color:#22c55e;'>approved</strong>.</p>"
						+ "<p>PG Owner ID: <strong style='color:#93c5fd;'>" + escapeHtml(pgOwnerCode) + "</strong></p>"
						+ login
						+ "<p style='font-size:12px;color:#94a3b8;'>You can now manage your PG listings from your dashboard.</p>");
		sendAsyncSafe(to, appName + " - PG Owner Account Approved", html);
	}

	@Async
	public void sendPgOwnerRejectedEmail(String toEmail, String ownerName, String reason) {
		String to = normalizeEmail(toEmail);
		String html = wrapTemplate("PG Owner Application Update",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>After review, your PG owner application was <strong style='color:#ef4444;'>not approved</strong> at this time.</p>"
						+ "<p><strong>Reason:</strong> " + escapeHtml(reason) + "</p>"
						+ "<p style='font-size:12px;color:#94a3b8;'>For questions, contact " + escapeHtml(supportEmail)
						+ "</p>");
		sendAsyncSafe(to, appName + " - PG Owner Application Status", html);
	}

	@Async
	public void sendAdminNewPgOwnerAlertEmail(com.realestate.main.entity.PgOwner owner) {
		if (!isMailConfigured() || fromEmail == null || fromEmail.isBlank()) {
			log.info("New PG owner pending approval: id={}, email={}, name={}", owner.getId(), owner.getEmail(),
					owner.getFullName());
			return;
		}
		String reviewLink = "";
		String base = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (base != null) {
			reviewLink = "<p><a href='" + escapeHtml(base + "/admin/pg-owners/" + owner.getId())
					+ "' style='color:#60a5fa;font-weight:700;'>Review application in Admin Portal</a></p>";
		}
		String pgName = owner.getPgName() != null ? owner.getPgName() : "Not provided";
		String pgType = owner.getPgType() != null ? owner.getPgType().name().replace('_', ' ') : "Not provided";
		String html = wrapTemplate("New PG Owner Awaiting Approval",
				"<p>A new PG owner registration requires your review.</p>"
						+ "<p><strong>Name:</strong> " + escapeHtml(owner.getFullName()) + "<br/>"
						+ "<strong>Email:</strong> " + escapeHtml(owner.getEmail()) + "<br/>"
						+ "<strong>PG Name:</strong> " + escapeHtml(pgName) + "<br/>"
						+ "<strong>Type:</strong> " + escapeHtml(pgType) + "<br/>"
						+ "<strong>City:</strong> " + escapeHtml(owner.getCity()) + ", " + escapeHtml(owner.getState())
						+ "</p>" + reviewLink);
		sendAsyncSafe(fromEmail, appName + " - New PG Owner Pending Approval", html);
	}

	private void sendAsyncSafe(String to, String subject, String html) {
		if (!isMailConfigured()) {
			log.debug("Async email skipped (not configured): {}", subject);
			return;
		}
		for (int port : portsToTry()) {
			try {
				sendOnPort(to, subject, html, port);
				log.info("Email sent to {} via {}:{} — {}", to, mailHost, port, subject);
				return;
			} catch (Exception e) {
				log.warn("Async SMTP port {} failed for {}: {}", port, to, e.getMessage());
			}
		}
		log.warn("Async email failed for all SMTP ports: {} — {}", to, subject);
	}

	/**
	 * User/admin OTP — bounded wait so HTTP requests do not hang on {@link java.net.ConnectException}.
	 */
	private OtpSendResult sendOtpDelivery(String to, String otp, String subject, String html) {
		return sendOtpDeliveryBounded(to, otp, subject, html);
	}

	/**
	 * Agent (and all registration) OTP: waits at most {@code app.mail.otp-max-wait-ms}, then returns
	 * on-screen OTP while SMTP keeps trying in the background.
	 */
	private OtpSendResult sendOtpDeliveryBounded(String to, String otp, String subject, String html) {
		if ("console".equalsIgnoreCase(deliveryMode)) {
			return consoleResult(to, otp);
		}

		if (!isMailConfigured()) {
			if (fallbackConsole) {
				return consoleResult(to, otp);
			}
			throw new EmailSendException(
					"Email not configured. Set spring.mail.username and spring.mail.password (Gmail App Password).");
		}

		Future<OtpSendResult> delivery = mailOtpExecutor.submit(() -> sendOtpDeliverySync(to, otp, subject, html));
		try {
			return delivery.get(otpMaxWaitMs, TimeUnit.MILLISECONDS);
		} catch (TimeoutException e) {
			delivery.cancel(true);
			log.warn("OTP SMTP timed out after {}ms for {}", otpMaxWaitMs, to);
			throw new EmailSendException(
					"Could not send OTP email in time. Check your internet connection and Gmail App Password, then try Resend OTP.");
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			delivery.cancel(true);
			throw new EmailSendException("OTP email delivery was interrupted. Please try Resend OTP.");
		} catch (ExecutionException e) {
			Throwable cause = e.getCause() != null ? e.getCause() : e;
			log.warn("OTP SMTP failed for {}: {} — {}", to, cause.getClass().getSimpleName(), cause.getMessage());
			if (fallbackConsole) {
				return consoleResult(to, otp);
			}
			if (cause instanceof EmailSendException emailEx) {
				throw emailEx;
			}
			throw new EmailSendException("Could not send OTP to " + to + ": " + cause.getMessage(), cause);
		}
	}

	private OtpSendResult sendOtpDeliverySync(String to, String otp, String subject, String html) {
		Exception lastError = null;
		for (int port : portsToTry()) {
			try {
				sendOnPort(to, subject, html, port);
				log.info("OTP email sent to {} via {}:{} subject={}", to, mailHost, port, subject);
				return OtpSendResult.sent(to);
			} catch (Exception e) {
				lastError = e;
				log.warn("SMTP failed on port {} for {}: {} — {}", port, to, e.getClass().getSimpleName(),
						e.getMessage());
			}
		}

		if (fallbackConsole) {
			log.warn("All SMTP ports failed for {}. OTP available on verify page only.", to, lastError);
			return consoleResult(to, otp);
		}

		throw new EmailSendException(
				"Could not send OTP to " + to
						+ ". Check Gmail App Password, spam folder, or network (ports 465/587).",
				lastError);
	}

	private OtpSendResult timedFallbackResult(String to, String otp) {
		log.warn("============================================================");
		log.warn("OTP (fast fallback — SMTP slow/blocked) for: {}", to);
		log.warn("OTP CODE: {}", otp);
		log.warn("============================================================");
		if (!exposeOtpInResponse) {
			return new OtpSendResult(false, null,
					"OTP generated. Email delivery timed out on this network — use Resend OTP or check logs.");
		}
		return OtpSendResult.timedFallback(otp, to);
	}

	@Async
	public void sendAdminWelcomeEmail(String toEmail, String adminName, String role) {
		String to = normalizeEmail(toEmail);
		String login = "";
		String base = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (base != null) {
			login = "<p><a href='" + escapeHtml(base + "/admin/login")
					+ "' style='color:#60a5fa;'>Login to Admin Portal</a></p>";
		}
		String html = wrapTemplate("Welcome Admin",
				"<p>Hello <strong>" + escapeHtml(adminName) + "</strong>,</p>"
						+ "<p>Your admin account is <strong style='color:#22c55e;'>activated</strong>.</p>"
						+ "<p>Role: <strong style='color:#93c5fd;'>" + escapeHtml(role) + "</strong></p>"
						+ login
						+ "<p style='font-size:12px;color:#94a3b8;'>Keep credentials confidential.</p>");
		try {
			sendOnPort(to, appName + " - Admin Welcome", html, mailPort);
		} catch (Exception e) {
			log.debug("Admin welcome email skipped: {}", e.getMessage());
		}
	}

	public boolean sendSimpleHtmlEmail(String toEmail, String subject, String htmlBody) {
		if (!isMailConfigured()) {
			log.info("Booking email skipped (mail not configured): {} — {}", toEmail, subject);
			return false;
		}
		try {
			sendOnPort(normalizeEmail(toEmail), subject, htmlBody, mailPort);
			return true;
		} catch (Exception e) {
			log.warn("Email send failed for {}: {}", toEmail, e.getMessage());
			return false;
		}
	}

	@Async
	public void sendWelcomeEmail(String toEmail, String userName, String referralCode) {
		String to = normalizeEmail(toEmail);
		String loginLink = "";
		String configuredBase = applicationUrlService.resolveConfiguredBaseUrlOrNull();
		if (configuredBase != null) {
			loginLink = "<p><a href='" + escapeHtml(configuredBase + "/user/login")
					+ "' style='color:#3b82f6;'>Login to your dashboard</a></p>";
		}
		String html = wrapTemplate("Welcome to " + appName,
				"<p>Hello <strong>" + escapeHtml(userName) + "</strong>,</p>"
						+ "<p>Your account is <strong style='color:#22c55e;'>activated</strong>.</p>"
						+ "<p>Your referral code: <strong style='color:#D4AF37;'>" + escapeHtml(referralCode) + "</strong></p>"
						+ loginLink);
		try {
			sendOnPort(to, appName + " - Welcome", html, mailPort);
		} catch (Exception e) {
			log.debug("Welcome email skipped for {}: {}", to, e.getMessage());
		}
	}

	private int[] portsToTry() {
		// Try STARTTLS (587) before SSL (465) — works better on some networks
		if (mailPort == 465) {
			return new int[] { 587, 465 };
		}
		if (mailPort == 587) {
			return new int[] { 587, 465 };
		}
		return new int[] { mailPort, 587, 465 };
	}

	private void sendOnPort(String to, String subject, String html, int port) throws Exception {
		JavaMailSender sender = port == mailPort ? mailSender
				: MailConfig.buildSender(mailHost, port, fromEmail, mailPassword, smtpConnectionTimeoutMs,
						smtpReadTimeoutMs);
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
		helper.setFrom(fromEmail, appName);
		helper.setTo(to);
		helper.setReplyTo(fromEmail);
		helper.setSubject(subject);
		helper.setText(html, true);
		sender.send(message);
	}

	private OtpSendResult consoleResult(String to, String otp) {
		log.warn("============================================================");
		log.warn("OTP (console delivery) for: {}", to);
		log.warn("OTP CODE: {}", otp);
		log.warn("Gmail SMTP is blocked on this network — use OTP shown on verify page.");
		log.warn("============================================================");
		if (!exposeOtpInResponse) {
			return new OtpSendResult(false, null,
					"OTP generated. Check server logs (SMTP blocked on this network).");
		}
		return OtpSendResult.consoleFallback(otp, to);
	}

	private String buildOtpHtml(String userName, String to, String otp) {
		return wrapTemplate("Email Verification",
				"<p>Hello <strong>" + escapeHtml(userName) + "</strong>,</p>"
						+ "<p>Your OTP for <strong>" + escapeHtml(to) + "</strong> is:</p>"
						+ "<p style='font-size:32px;font-weight:bold;color:#D4AF37;letter-spacing:8px;text-align:center;'>"
						+ otp + "</p>"
						+ "<p>This OTP expires in <strong>5 minutes</strong>. Do not share it with anyone.</p>");
	}

	private boolean isMailConfigured() {
		return fromEmail != null && !fromEmail.isBlank()
				&& mailPassword != null && !mailPassword.isBlank();
	}

	private String normalizeEmail(String email) {
		if (email == null || email.isBlank()) {
			throw new EmailSendException("Recipient email is empty");
		}
		return email.trim().toLowerCase();
	}

	private String escapeHtml(String text) {
		if (text == null) {
			return "";
		}
		return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
	}

	private String wrapTemplate(String title, String body) {
		return """
				<!DOCTYPE html><html><body style='font-family:Segoe UI,Arial;background:#0f172a;color:#fff;padding:24px;'>
				<div style='max-width:560px;margin:auto;background:#1e293b;border-radius:12px;padding:28px;border:1px solid #334155;'>
				<h2 style='color:#D4AF37;margin-top:0;'>%s</h2>%s
				<hr style='border-color:#334155;margin:24px 0;'/>
				<p style='color:#64748b;font-size:12px;'>%s | %s</p></div></body></html>
				""".formatted(title, body, appName, supportEmail);
	}

	@Async
	public void sendSubscriptionActivatedEmail(String toEmail, String agentName, String planName) {
		sendSimpleHtml(toEmail, appName + " — Subscription Activated",
				buildOtpHtml(agentName, toEmail, "Your " + planName + " plan is now active. You can post more properties from your agent dashboard."));
	}

	@Async
	public void sendSubscriptionPaymentEmail(String toEmail, String agentName, String planName, String invoiceNumber) {
		sendSimpleHtml(toEmail, appName + " — Payment Received",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Thank you for your payment for <strong>" + escapeHtml(planName) + "</strong>.</p>"
						+ "<p>Invoice: <strong>" + escapeHtml(invoiceNumber) + "</strong></p>");
	}

	@Async
	public void sendSubscriptionExpiringEmail(String toEmail, String agentName, String planName, int daysLeft) {
		sendSimpleHtml(toEmail, appName + " — Subscription Expiring Soon",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Your <strong>" + escapeHtml(planName) + "</strong> subscription expires in "
						+ daysLeft + " day(s). Renew from your dashboard to avoid interruption.</p>");
	}

	@Async
	public void sendSubscriptionExpiredEmail(String toEmail, String agentName, String planName) {
		sendSimpleHtml(toEmail, appName + " — Subscription Expired",
				"<p>Hello <strong>" + escapeHtml(agentName) + "</strong>,</p>"
						+ "<p>Your <strong>" + escapeHtml(planName) + "</strong> subscription has expired. "
						+ "You are back on the free plan (2 property listings).</p>");
	}

	@Async
	public void sendPgSubscriptionActivatedEmail(String toEmail, String ownerName, String planName) {
		sendSimpleHtml(toEmail, appName + " — PG Subscription Activated",
				buildOtpHtml(ownerName, toEmail, "Your " + planName + " plan is now active. You can add more PG listings from your owner dashboard."));
	}

	@Async
	public void sendPgSubscriptionPaymentEmail(String toEmail, String ownerName, String planName, String invoiceNumber) {
		sendSimpleHtml(toEmail, appName + " — PG Subscription Payment Received",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Thank you for your payment for <strong>" + escapeHtml(planName) + "</strong>.</p>"
						+ "<p>Invoice: <strong>" + escapeHtml(invoiceNumber) + "</strong></p>");
	}

	@Async
	public void sendPgSubscriptionExpiringEmail(String toEmail, String ownerName, String planName, int daysLeft) {
		sendSimpleHtml(toEmail, appName + " — PG Subscription Expiring Soon",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Your <strong>" + escapeHtml(planName) + "</strong> subscription expires in "
						+ daysLeft + " day(s). Renew from your dashboard to avoid interruption.</p>");
	}

	@Async
	public void sendPgSubscriptionExpiredEmail(String toEmail, String ownerName, String planName) {
		sendSimpleHtml(toEmail, appName + " — PG Subscription Expired",
				"<p>Hello <strong>" + escapeHtml(ownerName) + "</strong>,</p>"
						+ "<p>Your <strong>" + escapeHtml(planName) + "</strong> subscription has expired. "
						+ "You are back on the free plan (1 PG listing).</p>");
	}

	@Async
	public void sendPgBookingFullyPaidUserEmail(User user, PgBooking booking, PgProperty property, String sharingLabel,
			String guestNames) {
		String html = buildPgBookingPaidEmailBody(user.getFullName(), booking, property, sharingLabel, guestNames,
				"Your PG booking is fully confirmed. Payment has been received and your stay is approved.");
		sendSimpleHtml(user.getEmail(), appName + " — PG Booking Confirmed & Fully Paid", html);
	}

	@Async
	public void sendPgBookingFullyPaidOwnerEmail(PgOwner owner, User user, PgBooking booking, PgProperty property,
			String sharingLabel, String guestNames) {
		String html = buildPgBookingPaidEmailBody(owner.getFullName(), booking, property, sharingLabel, guestNames,
				"<strong>" + escapeHtml(user.getFullName()) + "</strong> has completed full payment for a PG booking at your property.");
		html = "<p>Hello <strong>" + escapeHtml(owner.getFullName()) + "</strong>,</p>"
				+ "<p><strong>" + escapeHtml(user.getFullName()) + "</strong> has completed full payment. "
				+ "The booking is now fully paid and confirmed.</p>"
				+ buildPgBookingDetailsTable(booking, property, sharingLabel, guestNames, user);
		sendSimpleHtml(owner.getEmail(), appName + " — PG Booking Fully Paid by Guest", html);
	}

	private String buildPgBookingPaidEmailBody(String recipientName, PgBooking booking, PgProperty property,
			String sharingLabel, String guestNames, String intro) {
		return "<p>Hello <strong>" + escapeHtml(recipientName) + "</strong>,</p>"
				+ "<p>" + intro + "</p>"
				+ buildPgBookingDetailsTable(booking, property, sharingLabel, guestNames, null);
	}

	private String buildPgBookingDetailsTable(PgBooking booking, PgProperty property, String sharingLabel,
			String guestNames, User user) {
		StringBuilder sb = new StringBuilder();
		sb.append("<table style='width:100%;border-collapse:collapse;margin:16px 0;font-size:14px;'>");
		row(sb, "Booking code", booking.getBookingCode());
		row(sb, "PG", property.getPgName() + " (" + property.getPgCode() + ")");
		row(sb, "Room", booking.getRoomNumber());
		row(sb, "Sharing", sharingLabel);
		row(sb, "Beds booked", String.valueOf(booking.getBedCount()) + " (#" + booking.getBedNumbers() + ")");
		if (user != null) {
			row(sb, "Guest", user.getFullName() + " · " + user.getMobile());
		}
		if (guestNames != null && !guestNames.isBlank()) {
			row(sb, "Occupants", guestNames);
		}
		row(sb, "Rent per bed", formatInrEmail(booking.getRentPerBed()));
		row(sb, "Security deposit", formatInrEmail(booking.getSecurityDepositAmount()));
		row(sb, "Total amount", formatInrEmail(booking.getTotalAmount()));
		row(sb, "Total paid", formatInrEmail(booking.getPaidAmount()));
		sb.append("</table>");
		sb.append("<p style='color:#22c55e;font-weight:700;'>Status: Fully paid — booking complete</p>");
		return sb.toString();
	}

	private static void row(StringBuilder sb, String label, String value) {
		sb.append("<tr><td style='padding:8px 0;color:#94a3b8;border-bottom:1px solid #334155;'>")
				.append(label)
				.append("</td><td style='padding:8px 0;font-weight:600;border-bottom:1px solid #334155;'>")
				.append(value)
				.append("</td></tr>");
	}

	private static String formatInrEmail(java.math.BigDecimal amount) {
		if (amount == null) {
			return "₹ 0";
		}
		return "₹ " + amount.setScale(0, java.math.RoundingMode.HALF_UP).toPlainString();
	}

	private void sendSimpleHtml(String toEmail, String subject, String bodyHtml) {
		if (!isMailConfigured()) {
			log.info("Email skipped (not configured): {} — {}", subject, toEmail);
			return;
		}
		sendAsyncSafe(normalizeEmail(toEmail), subject, wrapTemplate(subject, bodyHtml));
	}
}
