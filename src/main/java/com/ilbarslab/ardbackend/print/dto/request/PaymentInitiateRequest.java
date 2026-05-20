package com.ilbarslab.ardbackend.print.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentInitiateRequest {

    @NotNull
    private UUID orderId;

    // Kart bilgileri
    @NotNull
    private String cardHolderName;

    @NotNull
    private String cardNumber;

    @NotNull
    private String expireMonth;

    @NotNull
    private String expireYear;

    @NotNull
    private String cvc;

    // 3D Secure callback URL
    private String callbackUrl;
}