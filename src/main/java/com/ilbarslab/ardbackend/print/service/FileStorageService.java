package com.ilbarslab.ardbackend.print.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    /** İzin verilen subdir'ler — istemci keyfi yer yazamasın */
    private static final Set<String> ALLOWED_SUBDIRS = Set.of(
        "brand", "product", "category", "general"
    );

    /** İzin verilen MIME tipleri */
    private static final Set<String> ALLOWED_MIMES = Set.of(
        "image/jpeg", "image/jpg", "image/png", "image/webp", "image/gif"
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L; // 5 MB

    @PostConstruct
    public void init() {
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath();
            Files.createDirectories(base);
            for (String sub : ALLOWED_SUBDIRS) {
                Files.createDirectories(base.resolve(sub));
            }
            log.info("File storage initialized at: {}", base);
        } catch (IOException e) {
            log.error("Upload klasörü oluşturulamadı: {}", e.getMessage(), e);
        }
    }

    /**
     * Dosyayı diske kaydeder ve public URL döner.
     * URL formatı: {backendUrl}/uploads/{subdir}/{uuid}.{ext}
     */
    public String storeFile(MultipartFile file, String subdir) {
        validateFile(file);

        if (!ALLOWED_SUBDIRS.contains(subdir)) {
            subdir = "general";
        }

        try {
            String ext = getExtension(file.getOriginalFilename(), file.getContentType());
            String filename = UUID.randomUUID().toString() + "." + ext;

            Path base = Paths.get(uploadDir).toAbsolutePath();
            Path targetDir = base.resolve(subdir);
            Files.createDirectories(targetDir);

            Path target = targetDir.resolve(filename);
            // path traversal koruması — target base'in altında olmalı
            if (!target.normalize().startsWith(base.normalize())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz hedef yol");
            }

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Yüklendi: {}/{} ({} bytes)", subdir, filename, file.getSize());

            return backendUrl + "/uploads/" + subdir + "/" + filename;

        } catch (IOException e) {
            log.error("Dosya kaydedilemedi: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Dosya kaydedilemedi");
        }
    }

    /**
     * URL'den dosyayı sil. URL backendUrl prefix'i ile başlamıyorsa hiçbir şey yapma.
     */
    public void deleteByUrl(String url) {
        if (url == null || !url.startsWith(backendUrl + "/uploads/")) return;
        try {
            String relativePath = url.substring((backendUrl + "/uploads/").length());
            Path base = Paths.get(uploadDir).toAbsolutePath();
            Path target = base.resolve(relativePath).normalize();
            if (!target.startsWith(base)) return;  // path traversal koruması
            Files.deleteIfExists(target);
            log.info("Silindi: {}", target);
        } catch (Exception e) {
            log.warn("Silinemedi {}: {}", url, e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya boş");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                "Dosya çok büyük (maks 5 MB)");
        }
        String mime = file.getContentType();
        if (mime == null || !ALLOWED_MIMES.contains(mime.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Sadece resim dosyaları (JPG, PNG, WEBP, GIF)");
        }
    }

    private String getExtension(String filename, String mime) {
        if (filename != null && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            if (Set.of("jpg", "jpeg", "png", "webp", "gif").contains(ext)) {
                return ext;
            }
        }
        // MIME'dan tahmin
        if (mime == null) return "jpg";
        return switch (mime.toLowerCase()) {
            case "image/png"  -> "png";
            case "image/webp" -> "webp";
            case "image/gif"  -> "gif";
            default           -> "jpg";
        };
    }
}
