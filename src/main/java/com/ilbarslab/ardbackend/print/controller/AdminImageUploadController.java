package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/images")
@RequiredArgsConstructor
@Slf4j
public class AdminImageUploadController {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.public-url:http://localhost:8080}")
    private String publicUrl;

    private static final long MAX_SIZE = 10L * 1024 * 1024; // 10 MB
    private static final Set<String> ALLOWED_EXT = Set.of("jpg", "jpeg", "png", "webp", "gif", "svg");
    private static final Set<String> ALLOWED_FOLDERS = Set.of(
            "hero", "brand", "category", "general", "banner"
    );

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Map<String, String>>> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder
    ) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dosya boş"));
            }
            if (file.getSize() > MAX_SIZE) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Dosya 10 MB'dan büyük"));
            }
            if (!ALLOWED_FOLDERS.contains(folder)) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Geçersiz klasör"));
            }

            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.isBlank()) originalName = "image";

            String ext = extOf(originalName).toLowerCase().replace(".", "");
            if (!ALLOWED_EXT.contains(ext)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                        "Geçersiz dosya türü. Kabul edilenler: JPG, PNG, WEBP, GIF, SVG"));
            }

            UUID id = UUID.randomUUID();
            // ÖNEMLİ: toAbsolutePath() ile relative path sorununu çöz
            Path dir = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
            Files.createDirectories(dir);

            Path target = dir.resolve(id + "." + ext);
            file.transferTo(target.toFile());

            String url = publicUrl + "/uploads/" + folder + "/" + id + "." + ext;
            log.info("Admin image uploaded to {}: {}", target, url);

            return ResponseEntity.ok(ApiResponse.ok(Map.of("url", url, "originalName", originalName)));

        } catch (IOException e) {
            log.error("Image upload failed", e);
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Yüklenemedi: " + e.getMessage()));
        }
    }

    private static String extOf(String name) {
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : "";
    }
}