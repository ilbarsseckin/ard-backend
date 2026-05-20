package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private String status;
    private String paymentId;
    private String conversationId;
    private String htmlContent; // 3D Secure form
    private String errorMessage;
    private boolean success;
}