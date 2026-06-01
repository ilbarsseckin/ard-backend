package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "catalog_attribute_options", indexes = {
    @Index(name = "idx_cat_attr_opt_attr", columnList = "attribute_id"),
})

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogAttributeOption {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false)
    private CatalogAttribute attribute;

    @Column(nullable = false)
    private String value;            // "Siyah", "Mavi", "350g Mat Kuşe"

    @Column(name = "color_hex", length = 10)
    private String colorHex;         // "#000000" — renk seçimi için

    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "price_modifier", nullable = false, precision = 5, scale = 3)
    @Builder.Default
    private BigDecimal priceModifier = BigDecimal.ONE;
}
