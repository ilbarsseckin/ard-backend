package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
public class CatalogCategoryController {

    private final CatalogCategoryService service;

    /** Public ağaç — sadece aktif kategoriler. Navbar mega menüsü buradan beslenir. */
    @GetMapping("/tree")
    public ResponseEntity<?> getTree() {
        return ResponseEntity.ok(Map.of("data", service.getTree(true)));
    }

    /** Public detay — slug ile. Kategori sayfası buradan okur. */
    @GetMapping("/{slug}")
    public ResponseEntity<?> getBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(Map.of("data", service.getBySlug(slug)));
    }


}
