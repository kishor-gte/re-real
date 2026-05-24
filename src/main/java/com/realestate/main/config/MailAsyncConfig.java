package com.realestate.main.config;

import java.util.concurrent.ExecutorService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class MailAsyncConfig {

	@Bean(name = "mailOtpExecutor")
	ExecutorService mailOtpExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(4);
		executor.setQueueCapacity(100);
		executor.setThreadNamePrefix("mail-otp-");
		executor.initialize();
		return executor.getThreadPoolExecutor();
	}
}
