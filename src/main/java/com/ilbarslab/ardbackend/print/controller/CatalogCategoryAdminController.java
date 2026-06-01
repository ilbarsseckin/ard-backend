package com.ilbarslab.ardbackend.print.controller;


import com.ilbarslab.ardbackend.print.dto.response.CatalogCategoryResponse;
import com.ilbarslab.ardbackend.print.service.CatalogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin/catalog/categories")
@RequiredArgsConstructor
public class CatalogCategoryAdminController {

    private final CatalogCategoryService service;

    /** Düz liste — tüm kategoriler (aktif/pasif fark etmez) */
    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(Map.of("data", service.getAll()));
    }

    /** Ağaç görünüm — alt kategoriler nested halde */
    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(Map.of("data", service.getTree(false)));
    }

    /** ID ile detay */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.getById(id)));
    }

    /** Yeni kategori oluştur */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        CatalogCategoryResponse resp = service.create(body);
        return ResponseEntity.ok(Map.of("data", resp, "message", "Kategori oluşturuldu"));
    }

    /** Güncelle (partial — sadece gönderilen alanları değiştirir) */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        CatalogCategoryResponse resp = service.update(id, body);
        return ResponseEntity.ok(Map.of("data", resp, "message", "Kategori güncellendi"));
    }

    /** Sil (alt kategori veya ürünü varsa engelle) */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Kategori silindi"));
    }

    /** Aktif/pasif toggle */
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.toggleActive(id)));
    }

    /**
     * Toplu sıra güncelle — drag&drop için.
     * Body: [{ "id": "uuid", "sortOrder": 0 }, ...]
     */
    @PatchMapping("/reorder")
    public ResponseEntity<?> reorder(@RequestBody List<Map<String, Object>> orderList) {
        int updated = service.reorder(orderList);
        return ResponseEntity.ok(Map.of("message", updated + " kategori sırası güncellendi"));
    }
}
