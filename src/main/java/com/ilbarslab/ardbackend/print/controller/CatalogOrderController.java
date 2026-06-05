package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.repository.UserRepository;
import com.ilbarslab.ardbackend.print.service.CatalogOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/catalog/orders")
@RequiredArgsConstructor
public class CatalogOrderController {

    private final CatalogOrderService service;
    private final UserRepository userRepository;

    /**
     * Public katalog sipariş oluşturma.
     * Kullanıcı login ise userId atanır, değilse anonim.
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(Map.of(
            "data", service.create(body, userId),
            "message", "Siparişiniz alındı"
        ));
    }

    /** Giriş yapmış kullanıcının kendi siparişleri */
    @GetMapping("/my")
    public ResponseEntity<?> myOrders(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Giriş yapmanız gerekiyor"));
        }
        UUID userId = extractUserId(userDetails);
        if (userId == null) {
            return ResponseEntity.status(401).body(Map.of("message", "Kullanıcı bulunamadı"));
        }
        return ResponseEntity.ok(Map.of("data", service.listByUser(userId)));
    }

    /** Sipariş numarası ile takip (public — herhangi biri görebilir kendi numarasıyla) */
    @GetMapping("/track/{orderNumber}")
    public ResponseEntity<?> trackByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(Map.of("data", service.getByNumber(orderNumber)));
    }

    private UUID extractUserId(UserDetails userDetails) {
        if (userDetails == null) return null;
        return userRepository.findByEmail(userDetails.getUsername())
                .map(u -> u.getId())
                .orElse(null);
    }
}
