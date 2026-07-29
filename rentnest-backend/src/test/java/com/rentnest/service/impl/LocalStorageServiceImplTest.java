package com.rentnest.service.impl;

import com.rentnest.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LocalStorageServiceImplTest {

    @TempDir
    Path tempDir;

    private LocalStorageServiceImpl storageService;

    @BeforeEach
    void setUp() {
        storageService = new LocalStorageServiceImpl(tempDir.toString());
    }

    @Test
    void storeImageSuccess() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.webp",
                "image/webp",
                "some-binary-data".getBytes()
        );

        String url = storageService.storeImage(file);

        assertThat(url).startsWith("/uploads/");
        assertThat(url).endsWith(".webp");

        String fileName = url.substring(9);
        Path filePath = tempDir.resolve(fileName);
        assertThat(Files.exists(filePath)).isTrue();
    }

    @Test
    void storeImageThrowsOnEmptyFile() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "empty.png",
                "image/png",
                new byte[0]
        );

        assertThatThrownBy(() -> storageService.storeImage(file))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Cannot upload an empty file");
    }

    @Test
    void storeImageThrowsOnInvalidMimeType() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "doc.pdf",
                "application/pdf",
                "pdf-data".getBytes()
        );

        assertThatThrownBy(() -> storageService.storeImage(file))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Only JPG, PNG, and WEBP formats are allowed");
    }

    @Test
    void storeImageThrowsOnInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "malicious.exe",
                "image/png",
                "data".getBytes()
        );

        assertThatThrownBy(() -> storageService.storeImage(file))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Invalid file extension");
    }

    @Test
    void deleteImageSuccess() throws IOException {
        Path dummyFile = tempDir.resolve("dummy.jpg");
        Files.write(dummyFile, "data".getBytes());

        assertThat(Files.exists(dummyFile)).isTrue();

        storageService.deleteImage("/uploads/dummy.jpg");

        assertThat(Files.exists(dummyFile)).isFalse();
    }

    @Test
    void deleteImageQuietlyHandlesMissingFile() {
        // Should not throw any exception
        storageService.deleteImage("/uploads/non-existent.jpg");
    }
}
