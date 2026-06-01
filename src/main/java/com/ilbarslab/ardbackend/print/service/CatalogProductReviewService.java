package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CatalogProductReviewResponse;
import com.ilbarslab.ardbackend.print.dto.response.CatalogProductReviewStatsResponse;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProduct;
import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogProductReview;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogProductRepository;
import com.ilbarslab.ardbackend.print.entity.catalog.repository.CatalogProductReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CatalogProductReviewService {

    private final CatalogProductReviewRepository reviewRepo;
    private final CatalogProductRepository productRepo;

    // ─────────── PUBLIC: Listele ───────────

    @Transactional(readOnly = true)
    public List<CatalogProductReviewResponse> listForProduct(String slug) {
        CatalogProduct p = productRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));
        return reviewRepo.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(p.getId())
                .stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CatalogProductReviewStatsResponse getStats(String slug, UUID userId) {
        CatalogProduct p = productRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));

        Long total = reviewRepo.countByProductIdAndApprovedTrue(p.getId());
        Double avg = reviewRepo.findAverageRatingByProductId(p.getId());

        Map<Integer, Long> dist = new HashMap<>();
        for (int i = 1; i <= 5; i++) dist.put(i, 0L);
        for (Object[] row : reviewRepo.findRatingDistributionByProductId(p.getId())) {
            Integer r = ((Number) row[0]).intValue();
            Long c = ((Number) row[1]).longValue();
            dist.put(r, c);
        }

        Boolean canReview = null;
        Boolean alreadyReviewed = null;
        if (userId != null) {
            alreadyReviewed = reviewRepo.existsByProductIdAndUserId(p.getId(), userId);
            canReview = !alreadyReviewed && reviewRepo.hasUserPurchasedProduct(userId, p.getId());
        }

        return CatalogProductReviewStatsResponse.builder()
                .averageRating(avg == null ? 0.0 : Math.round(avg * 10.0) / 10.0)
                .totalCount(total)
                .distribution(dist)
                .canUserReview(canReview)
                .userAlreadyReviewed(alreadyReviewed)
                .build();
    }

    // ─────────── PUBLIC: Yorum gönder ───────────

    @Transactional
    public CatalogProductReviewResponse submitReview(
            UUID userId, String userEmail, String userName,
            String slug, Integer rating, String comment, Boolean anonymous) {

        CatalogProduct p = productRepo.findBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ürün bulunamadı"));

        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Giriş yapılmalı");
        }

        // 1) Aynı kullanıcı bu ürüne daha önce yorum yapmış mı?
        if (reviewRepo.existsByProductIdAndUserId(p.getId(), userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu ürüne zaten yorum yaptınız");
        }

        // 2) Kullanıcı bu ürünü satın almış mı?
        if (!reviewRepo.hasUserPurchasedProduct(userId, p.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sadece bu ürünü satın alan müşteriler yorum yapabilir");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Puan 1 ile 5 arasında olmalı");
        }
        if (comment == null || comment.trim().length() < 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yorum en az 10 karakter olmalı");
        }
        if (comment.length() > 2000) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Yorum çok uzun (max 2000 karakter)");
        }

        UUID orderId = reviewRepo.findLatestPurchaseOrderId(userId, p.getId());

        CatalogProductReview review = CatalogProductReview.builder()
                .product(p)
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .orderId(orderId)
                .rating(rating)
                .comment(comment.trim())
                .anonymous(Boolean.TRUE.equals(anonymous))
                .approved(true)
                .build();

        review = reviewRepo.save(review);
        log.info("Yorum oluşturuldu: user={}, product={}, rating={}", userId, p.getSlug(), rating);

        return toDto(review);
    }

    // ─────────── ADMIN ───────────

    @Transactional(readOnly = true)
    public List<CatalogProductReviewResponse> adminListAll() {
        return reviewRepo.findAllByOrderByCreatedAtDesc().stream().map(this::toDtoAdmin).toList();
    }

    @Transactional
    public CatalogProductReviewResponse adminApprove(UUID id, boolean approved) {
        CatalogProductReview r = reviewRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yorum bulunamadı"));
        r.setApproved(approved);
        return toDtoAdmin(reviewRepo.save(r));
    }

    @Transactional
    public void adminDelete(UUID id) {
        if (!reviewRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Yorum bulunamadı");
        }
        reviewRepo.deleteById(id);
    }

    // ─────────── private ───────────

    private CatalogProductReviewResponse toDto(CatalogProductReview r) {
        String name;
        if (Boolean.TRUE.equals(r.getAnonymous())) {
            name = "Anonim Kullanıcı";
        } else if (r.getUserName() != null && !r.getUserName().isBlank()) {
            name = maskName(r.getUserName());
        } else if (r.getUserEmail() != null && !r.getUserEmail().isBlank()) {
            name = maskEmail(r.getUserEmail());
        } else {
            name = "Müşteri";
        }
        return CatalogProductReviewResponse.builder()
                .id(r.getId())
                .productId(r.getProduct().getId())
                .rating(r.getRating())
                .comment(r.getComment())
                .displayName(name)
                .anonymous(r.getAnonymous())
                .createdAt(r.getCreatedAt())
                .build();
    }

    private CatalogProductReviewResponse toDtoAdmin(CatalogProductReview r) {
        CatalogProductReviewResponse dto = toDto(r);
        dto.setApproved(r.getApproved());
        return dto;
    }

    // "Ali Veli" → "Ali V."
    private String maskName(String full) {
        String[] parts = full.trim().split("\\s+");
        if (parts.length == 1) return parts[0];
        return parts[0] + " " + parts[parts.length - 1].charAt(0) + ".";
    }

    // "ali@gmail.com" → "ali***@gmail.com"
    private String maskEmail(String email) {
        int at = email.indexOf('@');
        if (at <= 0) return "Müşteri";
        String prefix = email.substring(0, at);
        String domain = email.substring(at);
        if (prefix.length() <= 3) return prefix + "***" + domain;
        return prefix.substring(0, 3) + "***" + domain;
    }
}
