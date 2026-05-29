package com.realestate.main.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

public class DBPropertiesEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String username = environment.getProperty("spring.datasource.username");
        String password = environment.getProperty("spring.datasource.password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            Map<String, Object> props = new HashMap<>();
            props.put("spring.flyway.enabled", "false");
            props.put("spring.jpa.hibernate.ddl-auto", "none");
            props.put("spring.jpa.generate-ddl", "false");
            environment.getPropertySources().addFirst(new MapPropertySource("db-properties-post-processor", props));
        }
    }
}
