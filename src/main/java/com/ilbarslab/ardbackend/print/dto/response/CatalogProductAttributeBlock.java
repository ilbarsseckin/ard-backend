package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Ürün detayında her bir öznitelik için: bu ürünün hangi seçenekleri desteklediği.
 * Örnek:
 *   { attrKey: "kagit", label: "Kağıt", inputType: "select",
 *     selectedOptions: [ {id, value:"350g Mat"}, {id, value:"400g Mat"} ] }
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductAttributeBlock {
    private UUID attributeId;
    private String attrKey;
    private String label;
    private String inputType;
    private Boolean required;
    private Integer sortOrder;
    private List<CatalogAttributeOptionResponse> selectedOptions;
}
