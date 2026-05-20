package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "product_configs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductType productType;

    // Form alanı anahtarı (material, coating, folding vb.)
    @Column(nullable = false)
    private String fieldKey;

    // TEXT | SELECT | NUMBER | BOOLEAN
    @Column(nullable = false)
    private String fieldType;

    // Select seçenekleri JSON formatında ["mat","parlak","selofan"]
    @Column(columnDefinition = "TEXT")
    private String options;

    private Boolean required;
    private Boolean affectsPrice;
    private Integer displayOrder;
}
