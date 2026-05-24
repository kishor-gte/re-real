package com.realestate.main.config;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

	private static final Logger log = LoggerFactory.getLogger(MailConfig.class);

	@Value("${spring.mail.host:smtp.gmail.com}")
	private String host;

	@Value("${spring.mail.port:587}")
	private int port;

	@Value("${spring.mail.username:}")
	private String username;

	@Value("${spring.mail.password:}")
	private String password;

	@Value("${app.mail.smtp.connection-timeout-ms:5000}")
	private int connectionTimeoutMs;

	@Value("${app.mail.smtp.read-timeout-ms:8000}")
	private int readTimeoutMs;

	@Bean
	public JavaMailSender javaMailSender() {
		return buildSender(host, port, username, password, connectionTimeoutMs, readTimeoutMs);
	}

	public static JavaMailSenderImpl buildSender(String host, int port, String username, String password) {
		return buildSender(host, port, username, password, 5000, 8000);
	}

	public static JavaMailSenderImpl buildSender(String host, int port, String username, String password,
			int connectionTimeoutMs, int readTimeoutMs) {
		JavaMailSenderImpl sender = new JavaMailSenderImpl();
		sender.setHost(host);
		sender.setPort(port);
		sender.setUsername(username);
		sender.setPassword(password);
		sender.setDefaultEncoding("UTF-8");

		Properties props = sender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", "true");
		String connectMs = String.valueOf(Math.max(1000, connectionTimeoutMs));
		String readMs = String.valueOf(Math.max(1000, readTimeoutMs));
		props.put("mail.smtp.connectiontimeout", connectMs);
		props.put("mail.smtp.timeout", readMs);
		props.put("mail.smtp.writetimeout", readMs);
		props.put("mail.smtp.ssl.trust", host);

		if (port == 465) {
			props.put("mail.smtp.ssl.enable", "true");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
		} else {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.starttls.required", "true");
			props.put("mail.smtp.ssl.enable", "false");
		}
		return sender;
	}

	@jakarta.annotation.PostConstruct
	void logMailStatus() {
		if (username == null || username.isBlank() || password == null || password.isBlank()) {
			log.warn("MAIL NOT CONFIGURED — set spring.mail.username and spring.mail.password");
		} else {
			log.info("Mail configured: {} via {}:{}", username, host, port);
		}
	}
}
