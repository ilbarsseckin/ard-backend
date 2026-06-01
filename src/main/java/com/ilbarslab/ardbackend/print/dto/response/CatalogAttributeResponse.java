package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogAttributeResponse {
    private UUID id;
    private UUID categoryId;
    private String attrKey;     // "kartus_rengi"
    private String label;       // "Kartuş Rengi"
    private String inputType;   // select, text, color, image
    private Boolean required;
    private Integer sortOrder;

    // Seçenekler (select/color tipi için)
    private List<CatalogAttributeOptionResponse> options;
}
