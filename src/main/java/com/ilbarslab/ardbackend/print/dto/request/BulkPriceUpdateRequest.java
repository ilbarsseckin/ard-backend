package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BulkPriceUpdateRequest {

    // null = tüm ürünler, değer = belirli kategori slug'ı
    private String categorySlug;

    // PERCENT_INCREASE | PERCENT_DECREASE | FIXED_INCREASE | FIXED_PRICE
    @NotBlank
    private String updateType;

    @NotNull
    @Positive
    private Double value;
}
