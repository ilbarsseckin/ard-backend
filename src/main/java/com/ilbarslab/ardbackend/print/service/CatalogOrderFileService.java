package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogOrderFileResponse;
import com.ilbarslab.ardbackend.print.entity.CatalogOrderFile;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrder;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogOrderRepository;
import com.ilbarslab.ardbackend.print.repository.CatalogOrderFileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogOrderFileService {

    private final CatalogOrderFileRepository fileRepo;
    private final CatalogOrderRepository orderRepo;

    @Value("${app.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${app.backend-url:http://localhost:8080}")
    private String backendUrl;

    private static final long MAX_FILE_SIZE = 50 * 1024 * 1024L; // 50 MB
    private static final int MAX_FILES_PER_ORDER = 20;

    private static final Set<String> ALLOWED_MIMES = Set.of(
        "application/pdf",
        "image/jpeg", "image/jpg", "image/png", "image/webp",
        "application/postscript",                          // .ai, .eps
        "application/illustrator",
        "application/octet-stream"                         // bazı browser'lar PDF için bunu gönderir
    );

    // ─────────── UPLOAD ───────────

    @Transactional
    public CatalogOrderFileResponse uploadFile(String orderNumber, MultipartFile file) {
        CatalogOrder order = orderRepo.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));

        validateFile(file);

        long count = fileRepo.countByOrderId(order.getId());
        if (count >= MAX_FILES_PER_ORDER) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Sipariş başına en fazla " + MAX_FILES_PER_ORDER + " dosya");
        }

        try {
            // Path: customer-designs/CAT-XXX/uuid.ext
            String origName = file.getOriginalFilename() == null ? "dosya" : file.getOriginalFilename();
            String ext = extractExtension(origName, file.getContentType());
            String storedName = UUID.randomUUID().toString() + "." + ext;
            String relativePath = "customer-designs/" + orderNumber + "/" + storedName;

            Path base = Paths.get(uploadDir).toAbsolutePath();
            Path target = base.resolve(relativePath);
            // path traversal koruması
            if (!target.normalize().startsWith(base.normalize())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz hedef yol");
            }
            Files.createDirectories(target.getParent());

            byte[] bytes = file.getBytes();
            Files.write(target, bytes);

            // PDF ise sayfa sayısı
            Integer pageCount = null;
            boolean isPdf = "application/pdf".equalsIgnoreCase(file.getContentType())
                         || origName.toLowerCase().endsWith(".pdf");
            if (isPdf) {
                pageCount = countPdfPages(bytes);
            } else if (file.getContentType() != null && file.getContentType().startsWith("image/")) {
                pageCount = 1;
            }

            // Sayfa sayısı tier qty'lerinden farklı mı? Uyarı flag
            boolean pageWarning = false;
            if (pageCount != null && pageCount > 1) {
                // 1'den fazla sayfa varsa admin'in dikkat etmesi gerekiyor
                pageWarning = true;
            }

            CatalogOrderFile entity = CatalogOrderFile.builder()
                .order(order)
                .originalName(origName)
                .storedFilename(storedName)
                .storagePath(relativePath)
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .pageCount(pageCount)
                .pageWarning(pageWarning)
                .build();
            entity = fileRepo.save(entity);

            log.info("Tasarım dosyası eklendi — sipariş: {}, dosya: {} ({} bytes, {} sayfa)",
                orderNumber, origName, file.getSize(), pageCount);

            return toResponse(entity, orderNumber);

        } catch (IOException e) {
            log.error("Dosya kaydedilemedi: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya kaydedilemedi");
        }
    }

    // ─────────── LIST ───────────

    @Transactional(readOnly = true)
    public List<CatalogOrderFileResponse> listByOrderNumber(String orderNumber) {
        CatalogOrder order = orderRepo.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return fileRepo.findByOrderIdOrderByCreatedAtAsc(order.getId()).stream()
            .map(f -> toResponse(f, orderNumber))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<CatalogOrderFileResponse> listByOrderId(UUID orderId) {
        CatalogOrder order = orderRepo.findById(orderId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sipariş bulunamadı"));
        return fileRepo.findByOrderIdOrderByCreatedAtAsc(orderId).stream()
            .map(f -> toResponse(f, order.getOrderNumber()))
            .toList();
    }

    // ─────────── DELETE ───────────

    @Transactional
    public void deleteFile(UUID fileId, String orderNumber) {
        CatalogOrderFile file = fileRepo.findById(fileId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dosya bulunamadı"));

        // Sahiplik kontrolü — orderNumber eşleşmeli
        if (orderNumber != null && !file.getOrder().getOrderNumber().equals(orderNumber)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Bu dosya size ait değil");
        }

        // Disk'ten sil
        try {
            Path base = Paths.get(uploadDir).toAbsolutePath();
            Path target = base.resolve(file.getStoragePath()).normalize();
            if (target.startsWith(base)) {
                Files.deleteIfExists(target);
            }
        } catch (Exception e) {
            log.warn("Disk'ten silinemedi {}: {}", file.getStoragePath(), e.getMessage());
        }

        fileRepo.delete(file);
        log.info("Tasarım dosyası silindi — sipariş: {}, dosya: {}",
            file.getOrder().getOrderNumber(), file.getOriginalName());
    }

    // ─────────── DOWNLOAD ───────────

    @Transactional(readOnly = true)
    public byte[] downloadFile(UUID fileId) {
        CatalogOrderFile file = fileRepo.findById(fileId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dosya bulunamadı"));

        try {
            Path base = Paths.get(uploadDir).toAbsolutePath();
            Path target = base.resolve(file.getStoragePath()).normalize();
            if (!target.startsWith(base)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Geçersiz yol");
            }
            return Files.readAllBytes(target);
        } catch (IOException e) {
            log.error("Dosya okunamadı: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Dosya okunamadı");
        }
    }

    @Transactional(readOnly = true)
    public CatalogOrderFile getFileEntity(UUID fileId) {
        return fileRepo.findById(fileId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dosya bulunamadı"));
    }

    // ─────────── private helpers ───────────

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dosya boş");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE,
                "Dosya çok büyük (maks 50 MB)");
        }
        String mime = file.getContentType();
        if (mime != null && !ALLOWED_MIMES.contains(mime.toLowerCase())) {
            // Bazı browser'lar generic application/octet-stream yollar; uzantıdan kontrol
            String name = file.getOriginalFilename();
            if (name == null || !isAllowedExtension(name)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Desteklenmeyen dosya tipi: " + mime);
            }
        }
    }

    private boolean isAllowedExtension(String name) {
        String lower = name.toLowerCase();
        return lower.endsWith(".pdf") || lower.endsWith(".ai") || lower.endsWith(".eps")
            || lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
            || lower.endsWith(".webp");
    }

    private String extractExtension(String filename, String mime) {
        if (filename != null && filename.contains(".")) {
            String ext = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
            if (Set.of("pdf", "ai", "eps", "jpg", "jpeg", "png", "webp").contains(ext)) {
                return ext;
            }
        }
        if (mime == null) return "bin";
        return switch (mime.toLowerCase()) {
            case "application/pdf"        -> "pdf";
            case "image/jpeg", "image/jpg"-> "jpg";
            case "image/png"              -> "png";
            case "image/webp"             -> "webp";
            case "application/postscript" -> "eps";
            default                       -> "bin";
        };
    }

    /** PDFBox 3.x ile sayfa sayısı sayar */
    private Integer countPdfPages(byte[] bytes) {
        try (PDDocument doc = Loader.loadPDF(bytes)) {
            return doc.getNumberOfPages();
        } catch (Exception e) {
            log.warn("PDF sayfa sayısı okunamadı: {}", e.getMessage());
            return null;
        }
    }

    private CatalogOrderFileResponse toResponse(CatalogOrderFile f, String orderNumber) {
        return CatalogOrderFileResponse.builder()
            .id(f.getId())
            .originalName(f.getOriginalName())
            .fileSize(f.getFileSize())
            .mimeType(f.getMimeType())
            .pageCount(f.getPageCount())
            .pageWarning(Boolean.TRUE.equals(f.getPageWarning()))
            .createdAt(f.getCreatedAt())
            .downloadUrl(backendUrl + "/api/catalog/orders/" + orderNumber + "/files/" + f.getId() + "/download")
            .build();
    }
}
