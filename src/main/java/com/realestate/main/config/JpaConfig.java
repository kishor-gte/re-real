package com.realestate.main.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {
		"com.realestate.main.repository",
		"com.realestate.main.rtc.repository"
})
public class JpaConfig {
}
