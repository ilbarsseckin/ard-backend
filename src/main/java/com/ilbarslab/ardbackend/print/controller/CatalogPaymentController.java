package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.CatalogPaymentInitiateRequest;
import com.ilbarslab.ardbackend.print.dto.response.CatalogPaymentResponse;
import com.ilbarslab.ardbackend.print.service.CatalogPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/catalog/payment")
@RequiredArgsConstructor
public class CatalogPaymentController {

    private final CatalogPaymentService service;

    /** 3DS başlatma — frontend kart bilgilerini gönderir, htmlContent döner */
    @PostMapping("/initiate")
    public ResponseEntity<?> initiate(@RequestBody CatalogPaymentInitiateRequest request) {
        CatalogPaymentResponse response = service.initiatePayment(request);
        return ResponseEntity.ok(Map.of("data", response));
    }
}
