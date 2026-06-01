package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogCategoryResponse {
    private UUID id;
    private String slug;
    private String name;
    private String icon;
    private String tagline;

    private UUID parentId;
    private String parentName;

    private Integer sortOrder;
    private Boolean active;

    // Sayım bilgileri (admin paneli için yararlı)
    private Long childCount;
    private Long productCount;

    // Tree endpoint'inde dolu, diğerlerinde null
    private List<CatalogCategoryResponse> children;

    private Instant createdAt;
    private Instant updatedAt;
}
