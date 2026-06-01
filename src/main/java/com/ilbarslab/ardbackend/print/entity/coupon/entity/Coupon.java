package com.ilbarslab.ardbackend.print.entity.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "coupons", indexes = {
    @Index(name = "idx_coupon_code", columnList = "code", unique = true),
    @Index(name = "idx_coupon_active", columnList = "active")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, length = 50)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponType type;

    /** % indirim (PERCENT) — ör. 10.00 = %10 */
    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercent;

    /** ₺ indirim (AMOUNT) — sabit tutar */
    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    /** Hediye kupon değeri (GIFT) — sipariş eşiği aşıldığında verilen kupon değeri */
    @Column(precision = 10, scale = 2)
    private BigDecimal giftAmount;

    /** Minimum sepet tutarı (kuponun kullanılabilmesi için) */
    @Column(precision = 10, scale = 2)
    private BigDecimal minOrderAmount;

    /** Toplam kullanım üst sınırı (null = sınırsız) */
    private Integer maxUsage;

    @Builder.Default
    private Integer currentUsage = 0;

    /** Kullanıcı başına kullanım hakkı (default 1) */
    @Builder.Default
    private Integer perUserLimit = 1;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Builder.Default
    private Boolean active = true;

    /** İlk ziyaret hediye kuponu olarak işaretliyse → welcome dialog'da gösterilir */
    @Builder.Default
    private Boolean autoIssueOnFirstVisit = false;

    /** Sipariş tutarı bu eşiği aşınca otomatik verilen GIFT kuponu için (null = otomatik değil) */
    @Column(precision = 10, scale = 2)
    private BigDecimal autoIssueOnOrderAmount;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ── Helper methods ──

    public boolean isValidNow() {
        if (!Boolean.TRUE.equals(active)) return false;
        LocalDateTime now = LocalDateTime.now();
        if (startDate != null && now.isBefore(startDate)) return false;
        if (endDate != null && now.isAfter(endDate)) return false;
        if (maxUsage != null && currentUsage != null && currentUsage >= maxUsage) return false;
        return true;
    }
}
