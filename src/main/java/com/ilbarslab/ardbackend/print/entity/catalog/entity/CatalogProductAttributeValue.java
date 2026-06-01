package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Ürünün hangi attribute seçeneklerini desteklediği
 * Örnek: "İdeal Cep Kaşeleri 38x14 mm ve 47x18 mm ebatlarda mevcut"
 */
@Entity
@Table(name = "catalog_product_attribute_values", indexes = {
    @Index(name = "idx_cat_pav_prod", columnList = "product_id"),
    @Index(name = "idx_cat_pav_attr", columnList = "attribute_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogProductAttributeValue {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogProduct product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private CatalogAttribute attribute;

    // Ürünün bu attribute için desteklediği seçenek
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private CatalogAttributeOption option;
}
