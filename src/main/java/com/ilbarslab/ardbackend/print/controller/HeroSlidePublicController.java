package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.HeroSlideService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/hero-slides")
@RequiredArgsConstructor
public class HeroSlidePublicController {

    private final HeroSlideService service;

    /** Ana sayfa için aktif slide'lar */
    @GetMapping
    public ResponseEntity<?> listActive() {
        return ResponseEntity.ok(Map.of("data", service.listActive()));
    }
}
