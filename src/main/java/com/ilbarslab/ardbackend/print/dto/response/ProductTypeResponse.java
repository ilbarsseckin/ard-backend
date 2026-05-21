package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeResponse {
    private UUID id;
    private String name;
    private String slug;
    private String pricingModel;
    private String unit;
    private Boolean hasFile;
    private Integer minOrder;
    private Boolean isActive;
    private String description;
    private String imageUrl;

}
