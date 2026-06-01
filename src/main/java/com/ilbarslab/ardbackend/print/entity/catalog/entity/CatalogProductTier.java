package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catalog_product_tiers", indexes = {
    @Index(name = "idx_cat_tier_prod", columnList = "product_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogProductTier {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogProduct product;

    @Column(nullable = false)
    private Integer qty;          // 500, 1000, 2000

    @Column(name = "price_usd", nullable = false, precision = 12, scale = 2)
    private BigDecimal priceUsd;  // O adet için TOPLAM USD fiyat

    @Builder.Default
    private Integer sortOrder = 0;
}
