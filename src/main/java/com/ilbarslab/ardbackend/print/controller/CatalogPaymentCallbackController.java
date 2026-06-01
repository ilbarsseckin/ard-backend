package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.CatalogPaymentService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;

/**
 * Iyzico 3DS callback'i bu endpoint'e POST olarak gelir.
 * Form-urlencoded body içinde paymentId, conversationData, conversationId bulunur.
 * Başarılı/başarısız sonuca göre frontend'e 302 redirect yapılır.
 */
@RestController
@RequestMapping("/api/webhook/catalog-payment")
@RequiredArgsConstructor
@Slf4j
public class CatalogPaymentCallbackController {

    private final CatalogPaymentService service;

    @Value("${app.frontend-url:http://localhost:3001}")
    private String frontendUrl;

    @PostMapping("/callback")
    public ResponseEntity<Void> callback(
            @RequestParam(required = false) String paymentId,
            @RequestParam(required = false) String conversationData,
            @RequestParam(required = false) String conversationId,
            @RequestParam(required = false) String status,
            HttpServletResponse response) throws IOException {

        log.info("Iyzico katalog callback geldi — paymentId: {}, conversationId: {}, status: {}",
                paymentId, conversationId, status);

        String orderNumber = service.getOrderNumberById(conversationId);
        if (orderNumber == null) orderNumber = "UNKNOWN";

        // Iyzico bazen "status=failure" gönderebilir 3DS aşamasında reddedilirse
        if (status != null && !"success".equalsIgnoreCase(status)) {
            log.warn("Iyzico callback başarısız status: {} - sipariş: {}", status, orderNumber);
            return redirectTo(frontendUrl + "/odeme-katalog?siparisNo=" + orderNumber + "&hata=3ds-iptal");
        }

        if (paymentId == null || conversationId == null) {
            log.warn("Iyzico callback eksik parametreler — paymentId: {}, convId: {}", paymentId, conversationId);
            return redirectTo(frontendUrl + "/odeme-katalog?siparisNo=" + orderNumber + "&hata=eksik-param");
        }

        boolean success = service.completePayment(paymentId, conversationData, conversationId);

        if (success) {
            return redirectTo(frontendUrl + "/siparis-tamamlandi?n=" + orderNumber);
        } else {
            return redirectTo(frontendUrl + "/odeme-katalog?siparisNo=" + orderNumber + "&hata=odeme-basarisiz");
        }
    }

    private ResponseEntity<Void> redirectTo(String url) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}
