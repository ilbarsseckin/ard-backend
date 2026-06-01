package com.ilbarslab.ardbackend.print.entity.coupon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_coupons", indexes = {
    @Index(name = "idx_user_coupon_user", columnList = "user_id"),
    @Index(name = "idx_user_coupon_coupon", columnList = "coupon_id"),
    @Index(name = "idx_user_coupon_used", columnList = "used")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCoupon {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CouponSource source;

    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();

    /** Bu kullanıcı için son kullanma tarihi (null = kuponun kendi endDate'i geçerli) */
    private LocalDateTime expiresAt;

    @Builder.Default
    private Boolean used = false;

    private LocalDateTime usedAt;

    /** Hangi siparişte kullanıldı */
    @Column(name = "order_id")
    private UUID orderId;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
