package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "price_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductType productType;

    // AREA_BASED | PACKAGE | TIERED_QUANTITY
    @Column(nullable = false)
    private String ruleType;

    // Alan bazlı: birim m² fiyatı
    private BigDecimal basePrice;

    // Paket: min adet
    private Integer minQty;

    // Paket: max adet (null = sınırsız)
    private Integer maxQty;

    // Tiered: bu aralık için birim fiyat
    private BigDecimal unitPrice;

    // Ek seçenek çarpanı (çift yüz, özel kesim vb.)
    private BigDecimal multiplier;

    // Ek sabit fiyat farkı (kaplama, katlama vb.)
    private BigDecimal priceDelta;

    // Hangi seçenek için (doublesided, coating, folding vb.)
    private String optionKey;

    // Seçenek değeri (glossy, matte, tri-fold vb.)
    private String optionValue;
}
