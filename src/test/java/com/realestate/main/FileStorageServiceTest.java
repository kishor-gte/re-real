package com.realestate.main;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.mock.web.MockMultipartFile;

import com.realestate.main.service.FileStorageService;

@SpringBootTest
@TestPropertySource(properties = { "APP_STORAGE_PROVIDER=local", "app.upload.dir=target/test-uploads/profiles", "app.upload.admin-dir=target/test-uploads/admin-profiles", "app.upload.agent-dir=target/test-uploads/agent", "app.upload.pg-owner-dir=target/test-uploads/pg-owner", "app.upload.property-dir=target/test-uploads/properties" })
class FileStorageServiceTest {

    @Autowired
    private FileStorageService fileStorageService;

    @AfterEach
    void cleanup() throws Exception {
        Path root = Path.of("target/test-uploads");
        if (Files.exists(root)) {
            Files.walk(root).sorted((a, b) -> b.compareTo(a)).forEach(p -> p.toFile().delete());
        }
    }

    @Test
    void storeProfileImage_localWritesFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "hello.jpg", "image/jpeg", "data".getBytes());
        String path = fileStorageService.storeProfileImage(file);
        assertThat(path).contains("/uploads/profiles/");
    }
}
