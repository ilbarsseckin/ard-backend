package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "catalog_attributes", indexes = {
    @Index(name = "idx_cat_attr_cat", columnList = "category_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogAttribute {
    @Id @GeneratedValue
    private UUID id;

    // Bu öznitelik hangi kategoriye ait
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CatalogCategory category;

    @Column(name = "attr_key", nullable = false, length = 80)
    private String attrKey;       // "kartus_rengi", "ebat"

    @Column(nullable = false)
    private String label;         // "Kartuş Rengi", "Ebat"

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String inputType = "select";  // select, text, color, image

    @Builder.Default
    private Boolean required = false;

    @Builder.Default
    private Integer sortOrder = 0;
}
