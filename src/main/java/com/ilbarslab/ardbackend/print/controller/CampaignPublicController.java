package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
public class CampaignPublicController {

    private final CampaignService service;

    /** Vitrin için aktif kampanyalar */
    @GetMapping
    public ResponseEntity<?> listActive() {
        return ResponseEntity.ok(Map.of("data", service.listActive()));
    }
}
