package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.CouponValidationResponse;
import com.ilbarslab.ardbackend.print.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponService service;

    // ─── PUBLIC ───

    /** İlk ziyarette gösterilecek welcome kupon */
    @GetMapping("/api/coupons/welcome")
    public ResponseEntity<?> getWelcome() {
        return ResponseEntity.ok(Map.of("data", service.getWelcomeCoupon()));
    }

    /** Aktif tüm kuponlar (kampanyalar sayfası) */
    @GetMapping("/api/coupons/active")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(Map.of("data", service.getActiveCoupons()));
    }

    /** Kupon kodunu doğrula (checkout'ta çağrılır) */
    @PostMapping("/api/coupons/validate")
    public ResponseEntity<?> validate(@RequestBody Map<String, Object> body, Authentication auth) {
        String code = body.get("code") != null ? body.get("code").toString() : null;
        BigDecimal cartTotal = body.get("cartTotal") != null
            ? new BigDecimal(body.get("cartTotal").toString())
            : BigDecimal.ZERO;
        UUID userId = extractUserId(auth);

        CouponValidationResponse resp = service.validate(code, cartTotal, userId);
        return ResponseEntity.ok(Map.of("data", resp));
    }

    // ─── ADMIN ───

    @GetMapping("/api/admin/coupons")
    public ResponseEntity<?> adminList() {
        return ResponseEntity.ok(Map.of("data", service.adminGetAll()));
    }

    @PostMapping("/api/admin/coupons")
    public ResponseEntity<?> adminCreate(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("data", service.adminCreate(body)));
    }

    @PutMapping("/api/admin/coupons/{id}")
    public ResponseEntity<?> adminUpdate(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("data", service.adminUpdate(id, body)));
    }

    @DeleteMapping("/api/admin/coupons/{id}")
    public ResponseEntity<?> adminDelete(@PathVariable UUID id) {
        service.adminDelete(id);
        return ResponseEntity.ok(Map.of("success", true));
    }

    // ─── HELPER ───

    private UUID extractUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try {
            Object principal = auth.getPrincipal();
            java.lang.reflect.Method getId = principal.getClass().getMethod("getId");
            Object id = getId.invoke(principal);
            return (id instanceof UUID) ? (UUID) id : UUID.fromString(id.toString());
        } catch (Exception e) {
            return null;
        }
    }
}
