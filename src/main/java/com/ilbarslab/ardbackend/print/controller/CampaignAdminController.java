package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.CampaignRequest;
import com.ilbarslab.ardbackend.print.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/campaigns")
@RequiredArgsConstructor
public class CampaignAdminController {

    private final CampaignService service;

    @GetMapping
    public ResponseEntity<?> listAll() {
        return ResponseEntity.ok(Map.of("data", service.listAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.getById(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CampaignRequest req) {
        return ResponseEntity.ok(Map.of(
            "data", service.create(req),
            "message", "Kampanya oluşturuldu"
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody CampaignRequest req) {
        return ResponseEntity.ok(Map.of(
            "data", service.update(id, req),
            "message", "Kampanya güncellendi"
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

    /** Sıralama — frontend yukarı/aşağı sonrası gönderir */
    @PostMapping("/reorder")
    public ResponseEntity<?> reorder(@RequestBody Map<String, List<UUID>> body) {
        List<UUID> ids = body.get("ids");
        if (ids == null) ids = List.of();
        service.reorder(ids);
        return ResponseEntity.ok(Map.of("message", "Sıralandı"));
    }
}
