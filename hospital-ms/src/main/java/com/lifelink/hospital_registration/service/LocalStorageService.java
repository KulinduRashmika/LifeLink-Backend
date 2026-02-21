package com.lifelink.hospital_registration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Slf4j
@Service
public class LocalStorageService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String save(MultipartFile file, String institutionName) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("Verification document is empty");
        }

        try {
            String original = file.getOriginalFilename() == null ? "document" : file.getOriginalFilename();

            String ext = "";
            int dot = original.lastIndexOf('.');
            if (dot >= 0) {
                ext = original.substring(dot);
            }

            String safeInstitution = (institutionName == null || institutionName.isBlank())
                    ? "unknown"
                    : institutionName.replaceAll("[^a-zA-Z0-9]", "_");

            String fileName = UUID.randomUUID() + ext;

            // uploads/<institutionName>/
            Path dir = Paths.get(uploadDir, safeInstitution);
            Files.createDirectories(dir);

            Path target = dir.resolve(fileName);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            log.info("Saved verification doc locally: {}", target.toAbsolutePath());

            // URL path served by Spring
            return "/uploads/" + safeInstitution + "/" + fileName;

        } catch (IOException e) {
            log.error("Failed to save file locally", e);
            throw new RuntimeException("Failed to save verification document locally", e);
        }
    }
}