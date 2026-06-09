package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.entity.SystemSetting;
import com.ilbarslab.ardbackend.print.repository.SystemSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class AdminSettingsController {

    private final SystemSettingRepository settingRepo;

    private static final String[] NOTIF_KEYS = {
        "admin_notification_phones",
        "admin_notification_emails",
        "delay_alert_threshold_days",
    };

    @GetMapping("/notifications")
    public ResponseEntity<?> getNotifications() {
        Map<String, String> result = new java.util.LinkedHashMap<>();
        for (String key : NOTIF_KEYS) {
            result.put(key, settingRepo.findById(key).map(SystemSetting::getValue).orElse(""));
        }
        return ResponseEntity.ok(Map.of("data", result));
    }

    @PostMapping("/notifications")
    public ResponseEntity<?> saveNotifications(@RequestBody Map<String, String> body) {
        for (String key : NOTIF_KEYS) {
            if (body.containsKey(key)) {
                String val = body.get(key);
                SystemSetting s = settingRepo.findById(key)
                    .orElse(SystemSetting.builder().key(key).description(key).build());
                s.setValue(val != null ? val : "");
                settingRepo.save(s);
            }
        }
        return ResponseEntity.ok(Map.of("message", "Kaydedildi"));
    }
}
