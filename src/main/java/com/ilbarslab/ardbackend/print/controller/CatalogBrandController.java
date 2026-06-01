package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogBrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/catalog/brands")
@RequiredArgsConstructor
public class CatalogBrandController {

    private final CatalogBrandService service;

    /** Public liste — sadece aktif markalar. Ürün filtrelerinde kullanılır. */
    @GetMapping
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(Map.of("data", service.getActive()));
    }

    /** Public detay — slug ile. */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(Map.of("data", service.getBySlug(slug)));
    }
}
