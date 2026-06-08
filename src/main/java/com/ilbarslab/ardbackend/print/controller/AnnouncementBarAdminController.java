package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.AnnouncementBarRequest;
import com.ilbarslab.ardbackend.print.service.AnnouncementBarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/announcement-bars")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AnnouncementBarAdminController {

    private final AnnouncementBarService service;

    @GetMapping
    public ResponseEntity<?> list() {
        return ResponseEntity.ok(Map.of("data", service.listAll()));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AnnouncementBarRequest req) {
        return ResponseEntity.ok(Map.of("data", service.create(req)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @RequestBody AnnouncementBarRequest req) {
        return ResponseEntity.ok(Map.of("data", service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.ok(Map.of("message", "Silindi"));
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<?> toggle(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("data", service.toggle(id)));
    }
}
