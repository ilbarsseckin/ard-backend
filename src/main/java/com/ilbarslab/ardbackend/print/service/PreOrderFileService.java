package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.PreOrderFileResponse;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.PreOrderFile;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.PreOrderFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PreOrderFileService {

    private final PreOrderFileRepository repo;

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @Value("${app.public-url:http://localhost:8080}")
    private String publicUrl;

    private static final long MAX_SIZE = 50L * 1024 * 1024; // 50 MB
    private static final Set<String> ALLOWED_EXT = Set.of(
            "pdf", "ai", "eps", "jpg", "jpeg", "png", "webp"
    );

    @Transactional
    public PreOrderFileResponse upload(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Dosya boş");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("Dosya 50 MB'dan büyük olamaz");
        }

        String originalName = file.getOriginalFilename();
        if (originalName == null || originalName.isBlank()) {
            originalName = "tasarim";
        }
        String ext = extOf(originalName).toLowerCase().replace(".", "");
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException(
                    "Geçersiz dosya türü. Kabul edilenler: PDF, AI, JPG, PNG, WEBP"
            );
        }

        UUID id = UUID.randomUUID();
        // ÖNEMLİ: toAbsolutePath() — Tomcat relative path'i temp klasörüne göre çözüyor, absolute lazım
        Path dir = Paths.get(uploadDir, "design").toAbsolutePath().normalize();
        Files.createDirectories(dir);

        Path target = dir.resolve(id + "." + ext);
        file.transferTo(target.toFile());

        PreOrderFile saved = repo.save(PreOrderFile.builder()
                .id(id)
                .originalName(originalName)
                .storedPath(target.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build());

        log.info("Pre-order file uploaded: {} ({}) -> {}", originalName, id, target);
        return PreOrderFileResponse.from(saved, publicUrl);
    }

    @Transactional
    public void delete(UUID id) throws IOException {
        PreOrderFile f = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dosya bulunamadı"));
        if (f.getClaimedByOrderId() != null) {
            throw new IllegalStateException("Siparişe bağlanmış dosya silinemez");
        }
        Files.deleteIfExists(Paths.get(f.getStoredPath()));
        repo.delete(f);
    }

    @Transactional
    public void claimByOrder(List<UUID> fileIds, UUID orderId) {
        if (fileIds == null || fileIds.isEmpty()) return;
        Instant now = Instant.now();
        for (UUID id : fileIds) {
            repo.findById(id).ifPresent(f -> {
                if (f.getClaimedByOrderId() == null) {
                    f.setClaimedByOrderId(orderId);
                    f.setClaimedAt(now);
                    repo.save(f);
                }
            });
        }
        log.info("Claimed {} files to order {}", fileIds.size(), orderId);
    }

    @Transactional(readOnly = true)
    public List<PreOrderFileResponse> getByOrder(UUID orderId) {
        return repo.findByClaimedByOrderId(orderId).stream()
                .map(f -> PreOrderFileResponse.from(f, publicUrl))
                .toList();
    }

    @Transactional
    public int cleanupOrphans() {
        Instant cutoff = Instant.now().minus(24, ChronoUnit.HOURS);
        List<PreOrderFile> orphans = repo.findByClaimedByOrderIdIsNullAndCreatedAtBefore(cutoff);
        for (PreOrderFile f : orphans) {
            try {
                Files.deleteIfExists(Paths.get(f.getStoredPath()));
            } catch (IOException e) {
                log.warn("Cleanup failed for {}: {}", f.getId(), e.getMessage());
            }
        }
        repo.deleteAll(orphans);
        return orphans.size();
    }

    private static String extOf(String name) {
        int i = name.lastIndexOf('.');
        return i >= 0 ? name.substring(i) : "";
    }
}