package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.PaymentInitiateRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.PaymentResponse;
import com.ilbarslab.ardbackend.print.service.IyzicoService;
import com.ilbarslab.ardbackend.print.service.WebhookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final IyzicoService iyzicoService;
    private final WebhookService webhookService;

    // Ödeme başlat
    @PostMapping("/api/payments/initiate")
    public ResponseEntity<ApiResponse<PaymentResponse>> initiatePayment(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody PaymentInitiateRequest request) {
        PaymentResponse response = iyzicoService.initiatePayment(request, userDetails.getUsername());
        if (response.isSuccess()) {
            return ResponseEntity.ok(ApiResponse.ok("Ödeme başlatıldı", response));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(response.getErrorMessage()));
        }
    }

    // 3D Secure callback — iyzico buraya POST atar
    @PostMapping("/api/webhook/payment/callback")
    public ResponseEntity<String> paymentCallback(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String conversationId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Map<String, String> allParams) {

        log.info("iyzico callback — params: {}", allParams);

        String result = webhookService.handle3dsCallback(paymentId, conversationId, status);

        // Frontend'e yönlendir
        if ("success".equals(result)) {
            return ResponseEntity.ok("<html><body><script>window.location='http://localhost:3000/order/success'</script></body></html>");
        } else {
            return ResponseEntity.ok("<html><body><script>window.location='http://localhost:3000/order/failed'</script></body></html>");
        }
    }
}