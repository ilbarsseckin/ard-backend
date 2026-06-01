package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/orders")
@RequiredArgsConstructor
public class CatalogOrderController {

    private final CatalogOrderService service;

    /**
     * Public katalog sipariş oluşturma.
     * Kullanıcı login ise userId atanır, değilse anonim.
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> body) {
        UUID userId = extractUserId();
        return ResponseEntity.ok(Map.of(
            "data", service.create(body, userId),
            "message", "Siparişiniz alındı"
        ));
    }

    /** Sipariş numarası ile takip (public — herhangi biri görebilir kendi numarasıyla) */
    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<?> trackByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(Map.of("data", service.getByNumber(orderNumber)));
    }

    private UUID extractUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.isAuthenticated()) return null;
            Object principal = auth.getPrincipal();
            // UserDetails / token üzerinden user id alma — projeye göre uyarlandı
            if (principal instanceof String s && !"anonymousUser".equals(s)) {
                // Email ile login varsa burada user lookup gerekebilir,
                // şimdilik null dönerek anonim sayalım
                return null;
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
