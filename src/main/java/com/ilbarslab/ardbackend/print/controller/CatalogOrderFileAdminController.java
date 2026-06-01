package com.ilbarslab.ardbackend.print.controller;


import com.ilbarslab.ardbackend.print.entity.CatalogOrderFile;
import com.ilbarslab.ardbackend.print.service.CatalogOrderFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

/**
 * Admin: sipariş dosyalarını görüntüle, indir, sil.
 * Yetki kontrolü SecurityConfig'te /api/admin/** üzerinden.
 */
@RestController
@RequestMapping("/api/admin/catalog/orders")
@RequiredArgsConstructor
public class CatalogOrderFileAdminController {

    private final CatalogOrderFileService service;

    @GetMapping("/{orderId}/files")
    public ResponseEntity<?> list(@PathVariable UUID orderId) {
        return ResponseEntity.ok(Map.of(
            "data", service.listByOrderId(orderId)
        ));
    }

    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<byte[]> download(@PathVariable UUID fileId) {
        CatalogOrderFile file = service.getFileEntity(fileId);
        byte[] bytes = service.downloadFile(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + file.getOriginalName().replace("\"", "") + "\"");
        if (file.getMimeType() != null) {
            headers.add(HttpHeaders.CONTENT_TYPE, file.getMimeType());
        } else {
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        }

        return ResponseEntity.ok().headers(headers).body(bytes);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> delete(@PathVariable UUID fileId) {
        service.deleteFile(fileId, null);  // admin için sahiplik kontrolü atlanır
        return ResponseEntity.ok(Map.of("message", "Silindi"));
    }
}
