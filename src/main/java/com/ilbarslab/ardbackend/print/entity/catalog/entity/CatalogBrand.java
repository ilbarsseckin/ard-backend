package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_brands", indexes = {
    @Index(name = "idx_cat_brand_slug", columnList = "slug", unique = true),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogBrand {
    @Id @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false, length = 100)
    private String slug;

    @Column(nullable = false)
    private String name;

    @Column(length = 500)
    private String logoUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private Boolean active = true;

    @CreationTimestamp private Instant createdAt;
}
