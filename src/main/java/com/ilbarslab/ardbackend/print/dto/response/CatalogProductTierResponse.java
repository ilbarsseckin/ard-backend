package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductTierResponse {
    private UUID id;
    private Integer qty;
    private BigDecimal priceUsd;
    private Integer sortOrder;
}
