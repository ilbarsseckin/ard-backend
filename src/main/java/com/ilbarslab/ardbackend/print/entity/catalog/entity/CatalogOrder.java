package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "catalog_orders", indexes = {
    @Index(name = "idx_catorder_status",      columnList = "status"),
    @Index(name = "idx_catorder_payment",     columnList = "payment_status"),
    @Index(name = "idx_catorder_created",     columnList = "created_at"),
    @Index(name = "idx_catorder_user",        columnList = "user_id"),
    @Index(name = "idx_catorder_number",      columnList = "order_number", unique = true),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogOrder {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "order_number", nullable = false, length = 32, unique = true)
    private String orderNumber;

    @Column(name = "customer_name", nullable = false, length = 100)
    private String customerName;

    @Column(name = "customer_phone", nullable = false, length = 30)
    private String customerPhone;

    @Column(name = "customer_email", length = 100)
    private String customerEmail;

    @Column(name = "customer_address", nullable = false, columnDefinition = "TEXT")
    private String customerAddress;

    @Column(name = "city", length = 60)
    private String city;

    @Column(name = "district", length = 60)
    private String district;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "subtotal_usd", precision = 12, scale = 2)
    private BigDecimal subtotalUsd;

    @Column(name = "total_tl", precision = 12, scale = 2)
    private BigDecimal totalTl;

    @Column(name = "usd_kur_at_order", precision = 10, scale = 4)
    private BigDecimal usdKurAtOrder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CatalogOrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    @Builder.Default
    private CatalogPaymentStatus paymentStatus = CatalogPaymentStatus.PENDING;

    /** Iyzico paymentId — callback'te yakalanır, refund için kullanılır */
    @Column(name = "iyzico_payment_id", length = 64)
    private String iyzicoPaymentId;

    /** Iyzico conversationData — 3DS tamamlama için callback'te gelir */
    @Column(name = "iyzico_conversation_data", columnDefinition = "TEXT")
    private String iyzicoConversationData;

    @Column(name = "user_id")
    private UUID userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<CatalogOrderItem> items = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "subtotal_tl", precision = 12, scale = 2)
    private BigDecimal subtotalTl;

    @Column(name = "discount_amount_tl", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal discountAmountTl = BigDecimal.ZERO;

    @Column(name = "coupon_code", length = 50)
    private String couponCode;
}
