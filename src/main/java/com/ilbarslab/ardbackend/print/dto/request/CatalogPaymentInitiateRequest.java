package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;

@Data
public class CatalogPaymentInitiateRequest {

    /** Sipariş numarası (CAT-XXXXXXXX) */
    private String orderNumber;

    /** Kart bilgileri */
    private String cardHolderName;
    private String cardNumber;
    private String expireMonth;  // "MM" formatında
    private String expireYear;   // "YY" veya "YYYY"
    private String cvc;

    /** 3DS callback URL — frontend'den geçer */
    private String callbackUrl;
}
