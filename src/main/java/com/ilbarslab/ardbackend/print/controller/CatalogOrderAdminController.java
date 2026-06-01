package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/catalog/orders")
@RequiredArgsConstructor
public class CatalogOrderAdminController {

    private final CatalogOrderService service;

    /** Tüm siparişleri listele — opsiyonel status filtresi */
    @GetMapping
    public ResponseEntity<?> list(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(Map.of("data", service.listAll(status)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.getById(id)));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        String newStatus = body.get("status") == null ? null : body.get("status").toString();
        return ResponseEntity.ok(Map.of(
            "data", service.updateStatus(id, newStatus),
            "message", "Durum güncellendi"
        ));
    }
}
