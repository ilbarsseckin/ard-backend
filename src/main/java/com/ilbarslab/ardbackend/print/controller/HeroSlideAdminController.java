package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.HeroSlideRequest;
import com.ilbarslab.ardbackend.print.service.HeroSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/hero-slides")
@RequiredArgsConstructor
public class HeroSlideAdminController {

    private final HeroSlideService service;

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(Map.of("data", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody HeroSlideRequest req) {
        return ResponseEntity.ok(Map.of(
            "data", service.create(req),
            "message", "Slide oluşturuldu"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody HeroSlideRequest req) {
        return ResponseEntity.ok(Map.of(
            "data", service.update(id, req),
            "message", "Slide güncellendi"
        ));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of(
            "data", service.toggleActive(id),
            "message", "Durum değişti"
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Silindi"));
    }

    /** Sıralama — frontend drag-drop sonrası gönderir */
    @PostMapping("/reorder")
    public ResponseEntity<?> reorder(@RequestBody Map<String, List<UUID>> body) {
        List<UUID> ids = body.get("ids");
        if (ids == null) ids = List.of();
        service.reorder(ids);
        return ResponseEntity.ok(Map.of("message", "Sıralandı"));
    }
}
