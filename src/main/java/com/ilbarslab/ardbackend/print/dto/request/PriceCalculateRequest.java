package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

@Data
public class PriceCalculateRequest {

    @NotBlank
    private String productSlug;

    // Alan bazlı için
    private Integer widthCm;
    private Integer heightCm;

    @NotNull
    @Positive
    private Integer quantity;

    // Ek seçenekler: {coating: "glossy", folding: "tri"}
    private Map<String, String> options;
}
