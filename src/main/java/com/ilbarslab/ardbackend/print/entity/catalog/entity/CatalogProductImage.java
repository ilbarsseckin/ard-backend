package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "catalog_product_images", indexes = {
    @Index(name = "idx_cat_img_prod", columnList = "product_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogProductImage {
    @Id @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private CatalogProduct product;

    @Column(nullable = false, length = 500)
    private String url;

    @Column(length = 200)
    private String altText;

    // İlk sıradaki (0) ana resim, ikinci sırada (1) hover'da görünür
    @Builder.Default
    private Integer sortOrder = 0;
}
