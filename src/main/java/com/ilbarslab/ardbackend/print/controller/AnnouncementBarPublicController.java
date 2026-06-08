package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.AnnouncementBarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/announcement-bars")
@RequiredArgsConstructor
public class AnnouncementBarPublicController {

    private final AnnouncementBarService service;

    @GetMapping
    public ResponseEntity<?> listActive() {
        return ResponseEntity.ok(Map.of("data", service.listActive()));
    }
}
