package com.realestate.main.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

public class AwsSecretsEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String provider = System.getenv("APP_SECRETS_PROVIDER");
        if (provider == null || !provider.equalsIgnoreCase("aws")) return;
        String secretId = System.getenv("APP_SECRETS_ID");
        if (secretId == null || secretId.isBlank()) return;
        try (SecretsManagerClient client = SecretsManagerClient.builder().region(Region.of(System.getenv().getOrDefault("APP_S3_REGION", "us-east-1"))).build()) {
            GetSecretValueRequest req = GetSecretValueRequest.builder().secretId(secretId).build();
            String secretString = client.getSecretValue(req).secretString();
            // expecting JSON map of properties
            Map<String, Object> map = new HashMap<>();
            if (secretString != null && secretString.trim().startsWith("{")) {
                // simple parse: split on quotes - for robust parsing include Jackson, but avoid extra dep here
                // We'll use Jackson ObjectMapper
                try {
                    com.fasterxml.jackson.databind.ObjectMapper om = new com.fasterxml.jackson.databind.ObjectMapper();
                    Map<String, String> json = om.readValue(secretString, Map.class);
                    map.putAll(json);
                } catch (Exception e) {
                    // ignore
                }
            }
            if (!map.isEmpty()) {
                MapPropertySource ps = new MapPropertySource("aws-secrets", (Map) map);
                environment.getPropertySources().addFirst(ps);
            }
        } catch (Exception e) {
            // ignore failures
        }
    }
}
