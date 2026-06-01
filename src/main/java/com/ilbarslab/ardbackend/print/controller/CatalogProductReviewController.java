package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.CatalogProductReviewResponse;
import com.ilbarslab.ardbackend.print.dto.response.CatalogProductReviewStatsResponse;
import com.ilbarslab.ardbackend.print.service.CatalogProductReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CatalogProductReviewController {

    private final CatalogProductReviewService reviewService;

    // ─────────── PUBLIC ───────────

    @GetMapping("/api/catalog/products/{slug}/reviews")
    public ResponseEntity<Map<String, Object>> listReviews(@PathVariable String slug) {
        List<CatalogProductReviewResponse> reviews = reviewService.listForProduct(slug);
        return ResponseEntity.ok(Map.of("data", reviews));
    }

    @GetMapping("/api/catalog/products/{slug}/reviews/stats")
    public ResponseEntity<Map<String, Object>> getStats(
            @PathVariable String slug,
            Authentication auth) {
        UUID userId = extractUserId(auth);
        CatalogProductReviewStatsResponse stats = reviewService.getStats(slug, userId);
        return ResponseEntity.ok(Map.of("data", stats));
    }

    @PostMapping("/api/catalog/products/{slug}/reviews")
    public ResponseEntity<Map<String, Object>> submitReview(
            @PathVariable String slug,
            @RequestBody Map<String, Object> body,
            Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Giriş yapılmalı");
        }

        UUID userId = extractUserId(auth);
        String userEmail = extractUserEmail(auth);
        String userName = extractUserName(auth);

        Integer rating = body.get("rating") == null ? null : ((Number) body.get("rating")).intValue();
        String comment = body.get("comment") == null ? null : body.get("comment").toString();
        Boolean anonymous = body.get("anonymous") == null ? false : Boolean.TRUE.equals(body.get("anonymous"));

        CatalogProductReviewResponse review = reviewService.submitReview(
                userId, userEmail, userName, slug, rating, comment, anonymous);

        return ResponseEntity.ok(Map.of("data", review, "message", "Yorumunuz başarıyla eklendi"));
    }

    // ─────────── ADMIN ───────────

    @GetMapping("/api/admin/catalog/reviews")
    public ResponseEntity<Map<String, Object>> adminList() {
        return ResponseEntity.ok(Map.of("data", reviewService.adminListAll()));
    }

    @PatchMapping("/api/admin/catalog/reviews/{id}/approve")
    public ResponseEntity<Map<String, Object>> adminApprove(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> body) {
        boolean approved = body.get("approved") == null ? true : Boolean.TRUE.equals(body.get("approved"));
        return ResponseEntity.ok(Map.of("data", reviewService.adminApprove(id, approved)));
    }

    @DeleteMapping("/api/admin/catalog/reviews/{id}")
    public ResponseEntity<Void> adminDelete(@PathVariable UUID id) {
        reviewService.adminDelete(id);
        return ResponseEntity.noContent().build();
    }

    // ─────────── private: auth extractors ───────────

    private UUID extractUserId(Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) return null;
        try {
            Object principal = auth.getPrincipal();
            // Case 1: principal'ın getId() metodu var (custom UserDetails)
            try {
                var idMethod = principal.getClass().getMethod("getId");
                Object id = idMethod.invoke(principal);
                if (id instanceof UUID u) return u;
                if (id != null) return UUID.fromString(id.toString());
            } catch (NoSuchMethodException ignored) {}
            // Case 2: principal'ın getUserId() metodu var
            try {
                var idMethod = principal.getClass().getMethod("getUserId");
                Object id = idMethod.invoke(principal);
                if (id instanceof UUID u) return u;
                if (id != null) return UUID.fromString(id.toString());
            } catch (NoSuchMethodException ignored) {}
        } catch (Exception ignored) {}
        return null;
    }

    private String extractUserEmail(Authentication auth) {
        if (auth == null) return null;
        try {
            Object principal = auth.getPrincipal();
            // Çoğu durumda principal'ın getEmail() veya getUsername()'i email döner
            try {
                var m = principal.getClass().getMethod("getEmail");
                Object v = m.invoke(principal);
                if (v != null) return v.toString();
            } catch (NoSuchMethodException ignored) {}
            return auth.getName(); // genelde email burda
        } catch (Exception e) {
            return null;
        }
    }

    private String extractUserName(Authentication auth) {
        if (auth == null) return null;
        try {
            Object principal = auth.getPrincipal();
            for (String methodName : new String[]{"getFullName", "getName", "getDisplayName"}) {
                try {
                    var m = principal.getClass().getMethod(methodName);
                    Object v = m.invoke(principal);
                    if (v != null && !v.toString().isBlank()) return v.toString();
                } catch (NoSuchMethodException ignored) {}
            }
            // First/last name'i ayrı tutuyorsa
            try {
                var fm = principal.getClass().getMethod("getFirstName");
                var lm = principal.getClass().getMethod("getLastName");
                Object f = fm.invoke(principal);
                Object l = lm.invoke(principal);
                if (f != null && l != null) return (f + " " + l).trim();
            } catch (NoSuchMethodException ignored) {}
        } catch (Exception ignored) {}
        return null;
    }
}
