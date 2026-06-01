package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponValidationResponse {
    private boolean valid;
    private String message;       // hata varsa açıklama
    private String code;
    private String name;
    private String type;
    private BigDecimal discountAmount;   // sepete uygulanacak indirim ₺
    private BigDecimal newTotal;         // indirim sonrası toplam
    private BigDecimal originalTotal;    // indirim öncesi toplam
}
