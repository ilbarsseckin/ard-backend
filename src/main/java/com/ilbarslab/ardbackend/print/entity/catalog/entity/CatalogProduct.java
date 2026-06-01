package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_products", indexes = {
    @Index(name = "idx_cat_prod_slug",  columnList = "slug", unique = true),
    @Index(name = "idx_cat_prod_cat",   columnList = "category_id"),
    @Index(name = "idx_cat_prod_brand", columnList = "brand_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogProduct {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CatalogCategory category;

    // Marka (opsiyonel — İdeal Cep Kaşeleri gibi markalı ürünler için)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private CatalogBrand brand;

    @Column(unique = true, nullable = false, length = 120)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String shortDesc;

    @Column(columnDefinition = "TEXT")
    private String longDesc;

    @Builder.Default
    private Boolean featured = false;

    @Column(length = 50)
    private String badge;                // "YENİ", "FLASH KAMPANYA"

    @Column(precision = 12, scale = 2)
    private BigDecimal originalPrice;    // İndirim gösterimi için (USD)

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp private Instant createdAt;
    @UpdateTimestamp   private Instant updatedAt;
}
