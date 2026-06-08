package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.service.InstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/installment")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentService service;

    @GetMapping
    public ResponseEntity<?> getInstallments(
            @RequestParam String binNumber,
            @RequestParam BigDecimal price) {
        return ResponseEntity.ok(Map.of("data", service.getInstallments(binNumber, price)));
    }
}
