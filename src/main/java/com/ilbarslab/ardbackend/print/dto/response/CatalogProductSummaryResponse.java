package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

/** Liste sayfaları için sade ürün DTO'su. */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductSummaryResponse {
    private UUID id;
    private String slug;
    private String name;
    private String shortDesc;
    private String categorySlug;

    private UUID categoryId;
    private String categoryName;

    private UUID brandId;
    private String brandName;

    // İlk resmin URL'i
    private String mainImageUrl;
    // İkinci resim (hover için)
    private String hoverImageUrl;

    // En düşük tier fiyatı (örn. "1000 adet 880 TL" göstergesi için)
    private BigDecimal minPriceUsd;
    private Integer minPriceQty;

    private Boolean featured;
    private String badge;
    private BigDecimal originalPrice;

    private Boolean active;
    private Integer sortOrder;

    // En çok satan endpoint için — diğer endpoint'lerde null olur
    private Long orderCount;
}