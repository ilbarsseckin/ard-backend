package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Map;

@Data
public class AddCartItemRequest {

    @NotBlank
    private String productSlug;

    private Integer widthCm;
    private Integer heightCm;

    @NotNull
    @Positive
    private Integer quantity;

    private Map<String, String> options;

    private Integer declaredPrints;

    public Integer getDeclaredPrints() {
        return declaredPrints == null ? 1 : declaredPrints;
    }
}
