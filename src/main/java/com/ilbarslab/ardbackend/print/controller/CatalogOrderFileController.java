package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.entity.CatalogOrderFile;
import com.ilbarslab.ardbackend.print.service.CatalogOrderFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/orders")
@RequiredArgsConstructor
public class CatalogOrderFileController {

    private final CatalogOrderFileService service;

    /** Tek dosya yükle (frontend birden fazla için ardışık çağırır) */
    @PostMapping(value = "/{orderNumber}/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(
            @PathVariable String orderNumber,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(Map.of(
            "data", service.uploadFile(orderNumber, file),
            "message", "Yüklendi"
        ));
    }

    /** Siparişe ait dosya listesi (metadata) */
    @GetMapping("/{orderNumber}/files")
    public ResponseEntity<?> list(@PathVariable String orderNumber) {
        return ResponseEntity.ok(Map.of(
            "data", service.listByOrderNumber(orderNumber)
        ));
    }

    /** Müşteri kendi dosyasını indirebilir (orderNumber + fileId biliyorsa) */
    @GetMapping("/{orderNumber}/files/{fileId}/download")
    public ResponseEntity<byte[]> download(
            @PathVariable String orderNumber,
            @PathVariable UUID fileId) {

        CatalogOrderFile file = service.getFileEntity(fileId);
        if (!file.getOrder().getOrderNumber().equals(orderNumber)) {
            return ResponseEntity.status(403).build();
        }
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

    /** Müşteri dosyasını siler — orderNumber query param ile sahiplik doğrulanır */
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<?> delete(
            @PathVariable UUID fileId,
            @RequestParam String orderNumber) {
        service.deleteFile(fileId, orderNumber);
        return ResponseEntity.ok(Map.of("message", "Silindi"));
    }
}
