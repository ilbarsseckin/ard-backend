package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Ürün detay sayfası için tam DTO — kategori, marka, attribute'lar, tier'lar, resimler. */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductResponse {
    private UUID id;
    private String slug;
    private String name;
    private String shortDesc;
    private String longDesc;

    // Kategori bilgisi (nested)
    private UUID categoryId;
    private String categorySlug;
    private String categoryName;
    private String categoryIcon;

    // Marka bilgisi (nested, opsiyonel)
    private UUID brandId;
    private String brandSlug;
    private String brandName;
    private String brandLogoUrl;

    // Kategoriye ait attribute'lar + bu ürünün desteklediği seçenekler
    private List<CatalogProductAttributeBlock> attributes;

    // Fiyat baremleri (qty asc)
    private List<CatalogProductTierResponse> tiers;

    // Resimler (sortOrder asc; ilk = ana, ikinci = hover)
    private List<CatalogProductImageResponse> images;

    // Kampanya alanları
    private Boolean featured;
    private String badge;
    private BigDecimal originalPrice;

    private Boolean active;
    private Integer sortOrder;

    private Instant createdAt;
    private Instant updatedAt;
}
