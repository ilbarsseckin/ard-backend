package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.request.AnnouncementBarRequest;
import com.ilbarslab.ardbackend.print.dto.response.AnnouncementBarResponse;
import com.ilbarslab.ardbackend.print.entity.AnnouncementBar;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.Coupon;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.CouponType;
import com.ilbarslab.ardbackend.print.entity.coupon.repository.CouponRepository;
import com.ilbarslab.ardbackend.print.repository.AnnouncementBarRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnnouncementBarService {

    private final AnnouncementBarRepository repo;
    private final CouponRepository couponRepo;

    public List<AnnouncementBarResponse> listActive() {
        Instant now = Instant.now();
        return repo.findByActiveTrueOrderBySortOrderAsc().stream()
                .filter(b -> b.getEndsAt() == null || now.isBefore(b.getEndsAt()))
                .map(this::toResponse)
                .toList();
    }

    public List<AnnouncementBarResponse> listAll() {
        return repo.findAllByOrderBySortOrderAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public AnnouncementBarResponse create(AnnouncementBarRequest req) {
        if (req.getMessage() == null || req.getMessage().isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mesaj zorunlu");

        AnnouncementBar bar = AnnouncementBar.builder()
                .message(req.getMessage())
                .subMessage(req.getSubMessage())
                .couponCode(req.getCouponCode())
                .bgColor(req.getBgColor() != null ? req.getBgColor() : "#F4821F")
                .textColor(req.getTextColor() != null ? req.getTextColor() : "#FFFFFF")
                .endsAt(req.getEndsAt())
                .active(req.getActive() != null ? req.getActive() : Boolean.TRUE)
                .sortOrder(req.getSortOrder() != null ? req.getSortOrder() : 0)
                .build();

        bar = repo.save(bar);

        // Kupon kodu varsa otomatik coupons tablosuna ekle
        if (req.getCouponCode() != null && !req.getCouponCode().isBlank()) {
            syncCoupon(req.getCouponCode().trim().toUpperCase(), req.getEndsAt(), true);
        }

        return toResponse(bar);
    }

    @Transactional
    public AnnouncementBarResponse update(UUID id, AnnouncementBarRequest req) {
        AnnouncementBar bar = find(id);
        String oldCode = bar.getCouponCode();

        if (req.getMessage() != null) bar.setMessage(req.getMessage());
        if (req.getSubMessage() != null) bar.setSubMessage(req.getSubMessage());
        if (req.getCouponCode() != null) bar.setCouponCode(req.getCouponCode());
        if (req.getBgColor() != null) bar.setBgColor(req.getBgColor());
        if (req.getTextColor() != null) bar.setTextColor(req.getTextColor());
        if (req.getEndsAt() != null) bar.setEndsAt(req.getEndsAt());
        if (req.getActive() != null) bar.setActive(req.getActive());
        if (req.getSortOrder() != null) bar.setSortOrder(req.getSortOrder());

        bar = repo.save(bar);

        // Yeni kupon kodu eklendiyse veya değiştiyse sync et
        String newCode = req.getCouponCode();
        if (newCode != null && !newCode.isBlank()) {
            String normalized = newCode.trim().toUpperCase();
            if (!normalized.equals(oldCode)) {
                syncCoupon(normalized, req.getEndsAt(), bar.getActive());
            } else {
                // Aynı kod ama aktiflik veya bitiş tarihi değişmiş olabilir
                updateCouponStatus(normalized, bar.getActive(), req.getEndsAt());
            }
        }

        return toResponse(bar);
    }

    @Transactional
    public void delete(UUID id) {
        AnnouncementBar bar = find(id);
        // Kuponu pasifleştir (silme — başka yerden kullanılmış olabilir)
        if (bar.getCouponCode() != null) {
            couponRepo.findByCodeIgnoreCase(bar.getCouponCode())
                    .ifPresent(c -> { c.setActive(false); couponRepo.save(c); });
        }
        repo.deleteById(id);
    }

    @Transactional
    public AnnouncementBarResponse toggle(UUID id) {
        AnnouncementBar bar = find(id);
        boolean newActive = !Boolean.TRUE.equals(bar.getActive());
        bar.setActive(newActive);
        bar = repo.save(bar);
        // Kuponu da aynı şekilde aktif/pasif yap
        if (bar.getCouponCode() != null) {
            updateCouponStatus(bar.getCouponCode(), newActive, bar.getEndsAt());
        }
        return toResponse(bar);
    }

    // ─── Yardımcı metodlar ───

    /** Kupon yoksa oluştur, varsa güncelle */
    private void syncCoupon(String code, Instant endsAt, Boolean active) {
        couponRepo.findByCodeIgnoreCase(code).ifPresentOrElse(
                existing -> {
                    // Zaten var — aktifliği güncelle
                    existing.setActive(Boolean.TRUE.equals(active));
                    if (endsAt != null) {
                        existing.setEndDate(java.time.LocalDateTime.ofInstant(endsAt, java.time.ZoneId.systemDefault()));
                    }
                    couponRepo.save(existing);
                    log.info("Duyuru kuponu güncellendi: {}", code);
                },
                () -> {
                    // Yok — oluştur (varsayılan: %10 indirim, sınırsız kullanım)
                    Coupon coupon = Coupon.builder()
                            .code(code)
                            .name("Duyuru Kuponu: " + code)
                            .type(CouponType.PERCENT)
                            .discountPercent(java.math.BigDecimal.TEN) // Varsayılan %10
                            .active(Boolean.TRUE.equals(active))
                            .currentUsage(0)
                            .perUserLimit(100)
                            .autoIssueOnFirstVisit(false)
                            .build();

                    if (endsAt != null) {
                        coupon.setEndDate(java.time.LocalDateTime.ofInstant(endsAt, java.time.ZoneId.systemDefault()));
                    }

                    couponRepo.save(coupon);
                    log.info("Duyuru kuponu oluşturuldu: {} (varsayılan %10 indirim)", code);
                }
        );
    }

    private void updateCouponStatus(String code, Boolean active, Instant endsAt) {
        couponRepo.findByCodeIgnoreCase(code).ifPresent(c -> {
            c.setActive(Boolean.TRUE.equals(active));
            if (endsAt != null) {
                c.setEndDate(java.time.LocalDateTime.ofInstant(endsAt, java.time.ZoneId.systemDefault()));
            }
            couponRepo.save(c);
        });
    }

    private AnnouncementBar find(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Banner bulunamadı"));
    }

    private AnnouncementBarResponse toResponse(AnnouncementBar b) {
        return AnnouncementBarResponse.builder()
                .id(b.getId())
                .message(b.getMessage())
                .subMessage(b.getSubMessage())
                .couponCode(b.getCouponCode())
                .bgColor(b.getBgColor())
                .textColor(b.getTextColor())
                .endsAt(b.getEndsAt())
                .active(b.getActive())
                .sortOrder(b.getSortOrder())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}