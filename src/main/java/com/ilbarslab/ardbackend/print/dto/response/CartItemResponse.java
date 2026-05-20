package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {
    private UUID id;
    private String productName;
    private String productSlug;
    private Integer widthCm;
    private Integer heightCm;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String priceBreakdown;
    private String fileOriginalName;
    private Integer filePagesCount;
    private Integer declaredPrints;
    private boolean hasFile;
    private boolean pageWarning; // sayfa != beyan
}
