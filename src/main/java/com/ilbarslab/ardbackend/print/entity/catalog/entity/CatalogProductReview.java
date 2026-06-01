package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_product_reviews", indexes = {
    @Index(name = "idx_review_product",  columnList = "product_id"),
    @Index(name = "idx_review_user",     columnList = "user_id"),
    @Index(name = "idx_review_approved", columnList = "approved"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogProductReview {

    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogProduct product;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_name", length = 255)
    private String userName;   // gönderim anında cache'lendi

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "order_id")
    private UUID orderId;       // hangi sipariş üzerinden yapıldı

    @Column(nullable = false)
    private Integer rating;     // 1-5

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Builder.Default
    @Column(nullable = false)
    private Boolean anonymous = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean approved = true;  // otomatik onaylı, admin silebilir

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (anonymous == null)  anonymous = false;
        if (approved == null)   approved = true;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}
