package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculateResponse {
    private String productSlug;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String priceBreakdown;
    private Integer widthCm;
    private Integer heightCm;
    private Double areaMq;
}
