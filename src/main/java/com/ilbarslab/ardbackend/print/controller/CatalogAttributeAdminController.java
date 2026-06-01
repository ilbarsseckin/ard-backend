package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/catalog")
@RequiredArgsConstructor
public class CatalogAttributeAdminController {

    private final CatalogAttributeService service;

    // ─── ATTRIBUTE endpoints ───

    /** Kategoriye ait tüm öznitelikleri (seçeneklerle birlikte) listele */
    @GetMapping("/categories/{categoryId}/attributes")
    public ResponseEntity<?> listByCategory(@PathVariable UUID categoryId) {
        return ResponseEntity.ok(Map.of("data", service.listByCategory(categoryId)));
    }

    /** Kategoriye yeni öznitelik ekle */
    @PostMapping("/categories/{categoryId}/attributes")
    public ResponseEntity<?> createAttribute(@PathVariable UUID categoryId,
                                              @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.createAttribute(categoryId, body),
            "message", "Öznitelik eklendi"
        ));
    }

    /** Öznitelik güncelle */
    @PutMapping("/attributes/{id}")
    public ResponseEntity<?> updateAttribute(@PathVariable UUID id,
                                              @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.updateAttribute(id, body),
            "message", "Güncellendi"
        ));
    }

    /** Öznitelik sil (kullanılıyorsa engellenir) */
    @DeleteMapping("/attributes/{id}")
    public ResponseEntity<?> deleteAttribute(@PathVariable UUID id) {
        service.deleteAttribute(id);
        return ResponseEntity.ok(Map.of("message", "Öznitelik silindi"));
    }

    // ─── OPTION endpoints ───

    /** Önitelik altına yeni seçenek ekle */
    @PostMapping("/attributes/{attributeId}/options")
    public ResponseEntity<?> createOption(@PathVariable UUID attributeId,
                                           @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.createOption(attributeId, body),
            "message", "Seçenek eklendi"
        ));
    }

    /** Seçenek güncelle */
    @PutMapping("/attribute-options/{id}")
    public ResponseEntity<?> updateOption(@PathVariable UUID id,
                                           @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.updateOption(id, body),
            "message", "Güncellendi"
        ));
    }

    /** Seçenek sil (kullanılıyorsa engellenir) */
    @DeleteMapping("/attribute-options/{id}")
    public ResponseEntity<?> deleteOption(@PathVariable UUID id) {
        service.deleteOption(id);
        return ResponseEntity.ok(Map.of("message", "Seçenek silindi"));
    }
}
