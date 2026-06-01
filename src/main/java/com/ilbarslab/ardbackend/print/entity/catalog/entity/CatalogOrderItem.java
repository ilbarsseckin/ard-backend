package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catalog_order_items", indexes = {
    @Index(name = "idx_catorderitem_order", columnList = "order_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogOrderItem {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CatalogOrder order;

    // Ürün referansı (silinme olasılığına karşı tüm gerekli bilgiler snapshot)
    @Column(name = "product_id")
    private UUID productId;

    @Column(name = "product_slug", nullable = false, length = 200)
    private String productSlug;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "main_image_url", length = 500)
    private String mainImageUrl;

    @Column(name = "category_id")
    private UUID categoryId;

    @Column(name = "category_slug", length = 200)
    private String categorySlug;

    @Column(name = "category_name", length = 200)
    private String categoryName;

    // Tier bilgisi
    @Column(name = "tier_id")
    private UUID tierId;

    @Column(name = "tier_qty", nullable = false)
    private Integer tierQty;

    @Column(name = "price_usd", precision = 12, scale = 2, nullable = false)
    private BigDecimal priceUsd;

    @Column(name = "price_tl", precision = 12, scale = 2)
    private BigDecimal priceTl;

    // Seçilen attribute'lar formatlı metin olarak
    // Örn: "Kağıt: 350g Mat Kuse; Renk: Kırmızı"
    @Column(name = "attributes_snapshot", columnDefinition = "TEXT")
    private String attributesSnapshot;
}
