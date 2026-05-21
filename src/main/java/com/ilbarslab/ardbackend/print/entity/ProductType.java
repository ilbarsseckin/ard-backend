package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "product_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    // AREA_BASED | PACKAGE | TIERED_QUANTITY | UNIT
    @Column(nullable = false)
    private String pricingModel;

    // m2 | adet | paket
    @Column(nullable = false)
    @Builder.Default
    private String unit = "adet";

    @Builder.Default
    private Boolean hasFile = true;

    @Builder.Default
    private Integer minOrder = 1;

    @Builder.Default
    private Boolean isActive = true;

    private String imageUrl;

    private String description;

    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<PriceRule> priceRules;

    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductConfig> configs;
}
