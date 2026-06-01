package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.service.SystemSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private final SystemSettingService settingService;

    @GetMapping("/public")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPublic() {
        Map<String, String> all = settingService.getAll();
        Map<String, String> pub = new java.util.HashMap<>();
        pub.put("usd_kur", all.getOrDefault("usd_kur", "45"));
        pub.put("references_show_text", all.getOrDefault("references_show_text", "true"));
        return ResponseEntity.ok(ApiResponse.ok(pub));
    }

    // Admin — tüm ayarlar
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(settingService.getAll()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> save(@RequestBody Map<String, String> settings) {
        Map<String, String> descriptions = Map.of(
                "usd_kur", "USD/TL Döviz Kuru",
                "references_show_text", "Referans yazılarını göster (true/false)"
        );
        settings.forEach((key, value) ->
                settingService.set(key, value, descriptions.getOrDefault(key, key))
        );
        return ResponseEntity.ok(ApiResponse.ok("Ayarlar kaydedildi", null));
    }
}
