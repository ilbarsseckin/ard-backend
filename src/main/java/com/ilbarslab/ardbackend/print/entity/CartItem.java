package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "cart_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_type_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private ProductType productType;

    private Integer widthCm;
    private Integer heightCm;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalPrice;

    // Seçilen opsiyonlar JSON: {"coating":"mat","doublesided":"true"}
    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    // Yüklenen dosya S3 key — ödeme öncesi kilitli
    private String fileS3Key;
    private String fileOriginalName;
    private Integer filePagesCount;

    // Müşterinin beyan ettiği baskı adedi
    @Builder.Default
    private Integer declaredPrints = 1;

    private String priceBreakdown;
}
