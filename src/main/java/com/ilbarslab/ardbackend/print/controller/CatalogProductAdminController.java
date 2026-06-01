package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/catalog/products")
@RequiredArgsConstructor
public class CatalogProductAdminController {

    private final CatalogProductService service;

    /**
     * Liste — opsiyonel filtreler: ?categoryId=&brandId=&activeOnly=true
     * Filtre yoksa tüm ürünleri döner.
     */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId,
            @RequestParam(required = false, defaultValue = "false") Boolean activeOnly) {
        return ResponseEntity.ok(Map.of("data", service.list(categoryId, brandId, activeOnly)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.getById(id)));
    }

    /**
     * Yeni ürün. Body içinde temel alanlar + attributeValues + tiers + images
     * gönderilebilir; tek bir transaction'da hepsi yazılır.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.create(body),
            "message", "Ürün oluşturuldu"
        ));
    }

    /** Güncelle (partial). Alt koleksiyonlar (attributeValues, tiers, images) gönderilirse replace edilir. */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.update(id, body),
            "message", "Ürün güncellendi"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Ürün silindi"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.toggleActive(id)));
    }

    // ─── Image yardımcı endpoint'leri (tek tek ekle/sil) ───

    @PostMapping("/{id}/images")
    public ResponseEntity<?> addImage(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of(
            "data", service.addImage(id, body),
            "message", "Resim eklendi"
        ));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable UUID imageId) {
        service.deleteImage(imageId);
        return ResponseEntity.ok(Map.of("message", "Resim silindi"));
    }
}
