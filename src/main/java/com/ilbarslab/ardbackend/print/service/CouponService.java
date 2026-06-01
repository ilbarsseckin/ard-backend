package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.CouponResponse;
import com.ilbarslab.ardbackend.print.dto.response.CouponValidationResponse;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.Coupon;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.CouponSource;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.CouponType;
import com.ilbarslab.ardbackend.print.entity.coupon.entity.UserCoupon;
import com.ilbarslab.ardbackend.print.entity.coupon.repository.CouponRepository;
import com.ilbarslab.ardbackend.print.entity.coupon.repository.UserCouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepo;
    private final UserCouponRepository userCouponRepo;

    // ─────────── PUBLIC READ ───────────

    /** İlk ziyarette gösterilecek welcome kupon */
    @Transactional(readOnly = true)
    public CouponResponse getWelcomeCoupon() {
        return couponRepo.findByActiveTrueAndAutoIssueOnFirstVisitTrue().stream()
            .filter(Coupon::isValidNow)
            .findFirst()
            .map(this::toResponse)
            .orElse(null);
    }

    /** Aktif tüm kuponlar (kampanyalar sayfası için) */
    @Transactional(readOnly = true)
    public List<CouponResponse> getActiveCoupons() {
        return couponRepo.findByActiveTrueOrderByCreatedAtDesc().stream()
            .filter(Coupon::isValidNow)
            .map(this::toResponse)
            .toList();
    }

    // ─────────── VALIDATION ───────────

    /**
     * Kupon kodunu doğrula ve sepete uygulanacak indirimi hesapla.
     * userId null gelirse guest user (sadece global limit kontrol edilir).
     */
    @Transactional(readOnly = true)
    public CouponValidationResponse validate(String code, BigDecimal cartTotal, UUID userId) {
        if (code == null || code.isBlank()) {
            return invalidResponse("Kupon kodu boş olamaz", cartTotal);
        }
        if (cartTotal == null || cartTotal.compareTo(BigDecimal.ZERO) <= 0) {
            return invalidResponse("Sepet tutarı geçersiz", cartTotal);
        }

        Coupon c = couponRepo.findByCodeIgnoreCase(code.trim()).orElse(null);
        if (c == null) {
            return invalidResponse("Geçersiz kupon kodu", cartTotal);
        }
        if (!c.isValidNow()) {
            return invalidResponse("Kupon süresi dolmuş veya devre dışı", cartTotal);
        }
        if (c.getMinOrderAmount() != null && cartTotal.compareTo(c.getMinOrderAmount()) < 0) {
            return invalidResponse(
                "Bu kupon için minimum sepet tutarı: ₺" + c.getMinOrderAmount().setScale(0, RoundingMode.HALF_UP),
                cartTotal
            );
        }

        // Kullanıcı bazlı limit (sadece login olmuş kullanıcı için)
        if (userId != null && c.getPerUserLimit() != null) {
            long usedCount = userCouponRepo.countByUserIdAndCouponIdAndUsedTrue(userId, c.getId());
            if (usedCount >= c.getPerUserLimit()) {
                return invalidResponse("Bu kuponu daha önce kullandınız", cartTotal);
            }
        }

        BigDecimal discount = calculateDiscount(c, cartTotal);
        BigDecimal newTotal = cartTotal.subtract(discount).max(BigDecimal.ZERO);

        return CouponValidationResponse.builder()
            .valid(true)
            .code(c.getCode())
            .name(c.getName())
            .type(c.getType().name())
            .discountAmount(discount.setScale(2, RoundingMode.HALF_UP))
            .newTotal(newTotal.setScale(2, RoundingMode.HALF_UP))
            .originalTotal(cartTotal.setScale(2, RoundingMode.HALF_UP))
            .build();
    }

    // ─────────── USE COUPON ───────────

    /**
     * Sipariş tamamlandığında kupon kullanımını işaretle.
     * userId null ise guest — sadece global usage++ yapılır.
     */
    @Transactional
    public void markUsed(String code, UUID userId, UUID orderId) {
        Coupon c = couponRepo.findByCodeIgnoreCase(code).orElse(null);
        if (c == null) return;

        c.setCurrentUsage((c.getCurrentUsage() == null ? 0 : c.getCurrentUsage()) + 1);
        couponRepo.save(c);

        if (userId != null) {
            // UserCoupon kaydı oluştur veya güncelle
            UserCoupon uc = userCouponRepo.findByUserIdAndCouponIdAndUsedFalse(userId, c.getId())
                .orElse(UserCoupon.builder()
                    .userId(userId)
                    .coupon(c)
                    .source(CouponSource.PROMO)
                    .build());
            uc.setUsed(true);
            uc.setUsedAt(LocalDateTime.now());
            uc.setOrderId(orderId);
            userCouponRepo.save(uc);
        }

        log.info("Kupon kullanıldı: {} (user: {}, order: {})", code, userId, orderId);
    }

    // ─────────── GIFT AUTO-ISSUE ───────────

    /**
     * Sipariş tutarı eşiği aşıyorsa kullanıcıya hediye kupon ata.
     * Order tamamlandıktan sonra çağrılır.
     */
    @Transactional
    public List<UserCoupon> issueGiftCouponsIfEligible(UUID userId, BigDecimal orderAmount) {
        if (userId == null || orderAmount == null) return List.of();

        List<Coupon> eligible = couponRepo
            .findByActiveTrueAndTypeAndAutoIssueOnOrderAmountIsNotNullAndAutoIssueOnOrderAmountLessThanEqual(
                CouponType.GIFT, orderAmount
            );

        List<UserCoupon> issued = new ArrayList<>();
        for (Coupon gift : eligible) {
            if (!gift.isValidNow()) continue;

            UserCoupon uc = UserCoupon.builder()
                .userId(userId)
                .coupon(gift)
                .source(CouponSource.GIFT)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMonths(3))  // 3 ay geçerli
                .used(false)
                .build();
            issued.add(userCouponRepo.save(uc));
            log.info("Hediye kupon verildi: user={}, coupon={}, value=₺{}",
                userId, gift.getCode(), gift.getGiftAmount());
        }
        return issued;
    }

    // ─────────── ADMIN CRUD ───────────

    @Transactional(readOnly = true)
    public List<CouponResponse> adminGetAll() {
        return couponRepo.findAll().stream()
            .sorted(Comparator.comparing(Coupon::getCreatedAt).reversed())
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public CouponResponse adminCreate(Map<String, Object> body) {
        String code = asString(body.get("code"));
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Kupon kodu zorunlu");
        }
        if (couponRepo.findByCodeIgnoreCase(code).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu kupon kodu zaten var: " + code);
        }
        CouponType type;
        try {
            type = CouponType.valueOf(asString(body.get("type")).toUpperCase());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Geçersiz kupon türü");
        }

        Coupon c = Coupon.builder()
            .code(code.trim().toUpperCase())
            .name(asString(body.get("name")))
            .description(asString(body.get("description")))
            .type(type)
            .discountPercent(asBigDecimal(body.get("discountPercent")))
            .discountAmount(asBigDecimal(body.get("discountAmount")))
            .giftAmount(asBigDecimal(body.get("giftAmount")))
            .minOrderAmount(asBigDecimal(body.get("minOrderAmount")))
            .maxUsage(asInt(body.get("maxUsage")))
            .perUserLimit(asInt(body.get("perUserLimit"), 1))
            .startDate(asDateTime(body.get("startDate")))
            .endDate(asDateTime(body.get("endDate")))
            .active(asBoolean(body.get("active"), true))
            .autoIssueOnFirstVisit(asBoolean(body.get("autoIssueOnFirstVisit"), false))
            .autoIssueOnOrderAmount(asBigDecimal(body.get("autoIssueOnOrderAmount")))
            .currentUsage(0)
            .build();

        c = couponRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public CouponResponse adminUpdate(UUID id, Map<String, Object> body) {
        Coupon c = couponRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kupon bulunamadı"));

        if (body.containsKey("name")) c.setName(asString(body.get("name")));
        if (body.containsKey("description")) c.setDescription(asString(body.get("description")));
        if (body.containsKey("discountPercent")) c.setDiscountPercent(asBigDecimal(body.get("discountPercent")));
        if (body.containsKey("discountAmount")) c.setDiscountAmount(asBigDecimal(body.get("discountAmount")));
        if (body.containsKey("giftAmount")) c.setGiftAmount(asBigDecimal(body.get("giftAmount")));
        if (body.containsKey("minOrderAmount")) c.setMinOrderAmount(asBigDecimal(body.get("minOrderAmount")));
        if (body.containsKey("maxUsage")) c.setMaxUsage(asInt(body.get("maxUsage")));
        if (body.containsKey("perUserLimit")) c.setPerUserLimit(asInt(body.get("perUserLimit"), 1));
        if (body.containsKey("startDate")) c.setStartDate(asDateTime(body.get("startDate")));
        if (body.containsKey("endDate")) c.setEndDate(asDateTime(body.get("endDate")));
        if (body.containsKey("active")) c.setActive(asBoolean(body.get("active"), true));
        if (body.containsKey("autoIssueOnFirstVisit"))
            c.setAutoIssueOnFirstVisit(asBoolean(body.get("autoIssueOnFirstVisit"), false));
        if (body.containsKey("autoIssueOnOrderAmount"))
            c.setAutoIssueOnOrderAmount(asBigDecimal(body.get("autoIssueOnOrderAmount")));

        c = couponRepo.save(c);
        return toResponse(c);
    }

    @Transactional
    public void adminDelete(UUID id) {
        Coupon c = couponRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kupon bulunamadı"));
        couponRepo.delete(c);
    }

    // ─────────── HELPERS ───────────

    private BigDecimal calculateDiscount(Coupon c, BigDecimal cartTotal) {
        BigDecimal d = BigDecimal.ZERO;
        switch (c.getType()) {
            case PERCENT -> {
                if (c.getDiscountPercent() != null) {
                    d = cartTotal.multiply(c.getDiscountPercent()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                }
            }
            case AMOUNT, GIFT -> {
                BigDecimal val = c.getType() == CouponType.GIFT ? c.getGiftAmount() : c.getDiscountAmount();
                if (val != null) {
                    d = val.min(cartTotal);
                }
            }
        }
        return d;
    }

    private CouponValidationResponse invalidResponse(String msg, BigDecimal cartTotal) {
        return CouponValidationResponse.builder()
            .valid(false)
            .message(msg)
            .originalTotal(cartTotal != null ? cartTotal.setScale(2, RoundingMode.HALF_UP) : null)
            .newTotal(cartTotal != null ? cartTotal.setScale(2, RoundingMode.HALF_UP) : null)
            .discountAmount(BigDecimal.ZERO)
            .build();
    }

    private CouponResponse toResponse(Coupon c) {
        return CouponResponse.builder()
            .id(c.getId())
            .code(c.getCode())
            .name(c.getName())
            .description(c.getDescription())
            .type(c.getType() != null ? c.getType().name() : null)
            .discountPercent(c.getDiscountPercent())
            .discountAmount(c.getDiscountAmount())
            .giftAmount(c.getGiftAmount())
            .minOrderAmount(c.getMinOrderAmount())
            .maxUsage(c.getMaxUsage())
            .currentUsage(c.getCurrentUsage())
            .perUserLimit(c.getPerUserLimit())
            .startDate(c.getStartDate())
            .endDate(c.getEndDate())
            .active(c.getActive())
            .autoIssueOnFirstVisit(c.getAutoIssueOnFirstVisit())
            .autoIssueOnOrderAmount(c.getAutoIssueOnOrderAmount())
            .createdAt(c.getCreatedAt())
            .build();
    }

    // ── Type converters ──
    private static String asString(Object v) { return v == null ? null : v.toString(); }

    private static Integer asInt(Object v) { return asInt(v, null); }

    private static Integer asInt(Object v, Integer fallback) {
        if (v == null) return fallback;
        try { return Integer.parseInt(v.toString().trim()); }
        catch (Exception e) { return fallback; }
    }

    private static BigDecimal asBigDecimal(Object v) {
        if (v == null) return null;
        try { return new BigDecimal(v.toString().trim()); }
        catch (Exception e) { return null; }
    }

    private static Boolean asBoolean(Object v, Boolean fallback) {
        if (v == null) return fallback;
        String s = v.toString().trim().toLowerCase();
        return s.equals("true") || s.equals("1");
    }

    private static LocalDateTime asDateTime(Object v) {
        if (v == null) return null;
        try { return LocalDateTime.parse(v.toString().trim()); }
        catch (Exception e) { return null; }
    }
}
