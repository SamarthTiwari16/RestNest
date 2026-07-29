package com.rentnest.service.impl;

import com.rentnest.exception.ValidationException;
import com.rentnest.service.ImageStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Service
public class LocalStorageServiceImpl implements ImageStorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageServiceImpl.class);

    private final Path uploadPath;
    private final String uploadDir;

    public LocalStorageServiceImpl(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = uploadDir;
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadPath);
            log.info("Initialized local upload path: {}", this.uploadPath);
        } catch (IOException e) {
            log.error("Could not create the upload directory: {}", this.uploadPath, e);
            throw new RuntimeException("Could not create the upload directory.", e);
        }
    }

    @Override
    public String storeImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ValidationException("Cannot upload an empty file");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp"))) {
            throw new ValidationException("Only JPG, PNG, and WEBP formats are allowed");
        }

        // Generate clean unique filename
        String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
        String cleanName = StringUtils.cleanPath(originalFilename);
        String extension = StringUtils.getFilenameExtension(cleanName);
        
        if (extension == null || (!extension.equalsIgnoreCase("jpg") && !extension.equalsIgnoreCase("jpeg") && !extension.equalsIgnoreCase("png") && !extension.equalsIgnoreCase("webp"))) {
            throw new ValidationException("Invalid file extension");
        }

        String uniqueFileName = UUID.randomUUID().toString() + "." + extension;

        try {
            Path targetLocation = this.uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            String url = "/uploads/" + uniqueFileName;
            log.info("Stored image: originalName={}, uniqueName={}, url={}", originalFilename, uniqueFileName, url);
            return url;
        } catch (IOException e) {
            log.error("Failed to store file: {}", uniqueFileName, e);
            throw new RuntimeException("Failed to store file: " + uniqueFileName, e);
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl != null && imageUrl.startsWith("/uploads/")) {
            String fileName = imageUrl.substring(9); // remove "/uploads/"
            try {
                Path filePath = this.uploadPath.resolve(fileName).normalize();
                if (Files.deleteIfExists(filePath)) {
                    log.info("Deleted image from local storage: {}", fileName);
                } else {
                    log.warn("Image file not found for deletion: {}", fileName);
                }
            } catch (IOException e) {
                log.error("Could not delete file: {}. Error: {}", fileName, e.getMessage(), e);
            }
        }
    }
}
