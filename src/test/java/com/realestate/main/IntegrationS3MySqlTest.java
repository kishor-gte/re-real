package com.realestate.main;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.realestate.main.service.FileStorageService;

import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class IntegrationS3MySqlTest {

    static final LocalStackContainer localstack = new LocalStackContainer(DockerImageName.parse("localstack/localstack:2.2.0"))
            .withServices(LocalStackContainer.Service.S3);

    static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0").withDatabaseName("testdb").withUsername("test").withPassword("test");

    static {
        localstack.start();
        mysql.start();
    }

    @Autowired
    private FileStorageService fileStorageService;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("APP_STORAGE_PROVIDER", () -> "s3");
        registry.add("APP_S3_BUCKET", () -> "test-bucket");
        registry.add("APP_S3_REGION", () -> localstack.getRegion());
        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mysql.getUsername());
        registry.add("spring.datasource.password", () -> mysql.getPassword());
    }

    @Test
    void s3UploadAndPresign() throws Exception {
        // create bucket via AWS SDK S3 client against LocalStack
        try (S3Client s3Client = S3Client.builder()
                .endpointOverride(localstack.getEndpointOverride(LocalStackContainer.Service.S3))
                .region(Region.of(localstack.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(localstack.getAccessKey(), localstack.getSecretKey())))
                .build()) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket("test-bucket").build());
        }
        byte[] data = "hello".getBytes();
        String result = fileStorageService.storeProfileImage(new org.springframework.mock.web.MockMultipartFile("file", "hello.jpg", "image/jpeg", data));
        assertThat(result).isNotNull();
        assertThat(result).contains("http");
    }
}
