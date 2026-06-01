package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_categories", indexes = {
    @Index(name = "idx_cat_cat_slug",   columnList = "slug", unique = true),
    @Index(name = "idx_cat_cat_parent", columnList = "parent_id"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogCategory {
    @Id @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Column(nullable = false)
    private String name;

    private String icon;        // emoji veya ikon adı (örn. "🪪")
    private String tagline;     // alt başlık

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private CatalogCategory parent;

    @Builder.Default
    private Integer sortOrder = 0;

    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp private Instant createdAt;
    @UpdateTimestamp   private Instant updatedAt;
}
