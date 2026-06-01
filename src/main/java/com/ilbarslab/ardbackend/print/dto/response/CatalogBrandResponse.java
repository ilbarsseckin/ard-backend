package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogBrandResponse {
    private UUID id;
    private String slug;
    private String name;
    private String logoUrl;
    private String description;
    private Boolean active;

    // Kullanım sayısı (kaç ürün bu markayı kullanıyor)
    private Long productCount;

    private Instant createdAt;
}
