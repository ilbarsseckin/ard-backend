package com.ilbarslab.ardbackend.print.dto.response;

import com.ilbarslab.ardbackend.print.dto.PriceTierDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTypeResponse {
    private UUID id;
    private String name;
    private String slug;
    private String pricingModel;
    private String unit;
    private Boolean hasFile;
    private Integer minOrder;
    private Boolean isActive;
    private String description;
    private String imageUrl;

    /** İlk baremin USD fiyatı — geriye uyumluluk için (tek fiyatlı modeller burada da görür) */
    private BigDecimal basePrice;

    /** Tüm baremler (USD) — admin formundaki tier tablosu bunu kullanır */
    private List<PriceTierDto> priceTiers;


    private Boolean featured;
    private String badge;
    private BigDecimal originalPrice;
}