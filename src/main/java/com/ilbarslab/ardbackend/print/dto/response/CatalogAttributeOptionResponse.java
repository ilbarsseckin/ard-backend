package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogAttributeOptionResponse {
    private UUID id;
    private UUID attributeId;
    private String value;
    private String colorHex;
    private Integer sortOrder;
    private BigDecimal priceModifier;
    
}
