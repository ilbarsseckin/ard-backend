package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/products")
@RequiredArgsConstructor
public class CatalogProductController {

    private final CatalogProductService service;

    /** Public liste — sadece aktif ürünler. Opsiyonel: ?categoryId=&brandId= */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID brandId) {
        return ResponseEntity.ok(Map.of("data", service.list(categoryId, brandId, true)));
    }

    /** Öne çıkan ürünler — anasayfa için */
    @GetMapping("/featured")
    public ResponseEntity<?> getFeatured() {
        return ResponseEntity.ok(Map.of("data", service.getFeatured()));
    }

    /** En çok satan ürünler — anasayfa için */
    @GetMapping("/best-sellers")
    public ResponseEntity<?> getBestSellers(@RequestParam(defaultValue = "8") int limit) {
        return ResponseEntity.ok(Map.of("data", service.listBestSellers(limit)));
    }

    /** Public detay — slug ile (en sonda olmalı, yoksa /featured /best-sellers slug zanneder) */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(Map.of("data", service.getBySlug(slug)));
    }
}