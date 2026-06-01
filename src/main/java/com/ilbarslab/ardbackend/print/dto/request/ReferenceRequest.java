package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReferenceRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String sector;

    @NotBlank
    private String category;

    private String description;
    private String logoUrl;        // dosya yüklenmediğinde URL ile logo
    private String color;
    private String abbr;
    private Boolean featured = false;
    private Boolean active = true;
    private Boolean showText = true;
    private Integer displayOrder = 0;
}