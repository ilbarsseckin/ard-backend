package com.ilbarslab.ardbackend.print.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceTierDto {
    private Integer minQty;          // null = alt sınır yok
    private Integer maxQty;          // null = sınırsız (en üst barem)
    private BigDecimal price;        // USD — usd_kur ile TL'ye çevrilir
}