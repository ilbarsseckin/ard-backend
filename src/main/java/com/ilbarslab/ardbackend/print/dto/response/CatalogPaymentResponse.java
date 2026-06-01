package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogPaymentResponse {
    private String status;          // pending_3ds | failed | error
    private String orderNumber;
    private String conversationId;
    private String htmlContent;     // base64 veya plain HTML (Iyzico 3DS sayfası)
    private String errorMessage;
    private boolean success;
}
