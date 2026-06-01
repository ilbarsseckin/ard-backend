package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogOrderItemResponse {
    private UUID id;
    private UUID productId;
    private String productSlug;
    private String productName;
    private String mainImageUrl;
    private String categoryName;
    private String categorySlug;
    private Integer tierQty;
    private BigDecimal priceUsd;
    private BigDecimal priceTl;
    private String attributesSnapshot;
}
