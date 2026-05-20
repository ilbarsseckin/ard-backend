package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.request.UpdateOrderStatusRequest;
import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.OrderDetailResponse;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.service.OperatorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/operator")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OPERATOR','ADMIN')")
public class OperatorController {

    private final OperatorService operatorService;

    // Tüm siparişler — opsiyonel durum filtresi
    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDetailResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(operatorService.getAllOrders(status)));
    }

    // Sipariş detayı
    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(operatorService.getOrderDetail(id)));
    }

    // Durum güncelle
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderDetailResponse response = operatorService.updateStatus(
                id, request.getStatus(), request.getNote());
        return ResponseEntity.ok(ApiResponse.ok("Durum güncellendi", response));
    }

    // Onayla
    @PostMapping("/orders/{id}/approve")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> approve(
            @PathVariable UUID id) {
        OrderDetailResponse response = operatorService.updateStatus(
                id, OrderStatus.REVIEWING, "Sipariş onaylandı, incelemeye alındı");
        return ResponseEntity.ok(ApiResponse.ok("Sipariş onaylandı", response));
    }

    // Reddet
    @PostMapping("/orders/{id}/reject")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> reject(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        OrderDetailResponse response = operatorService.updateStatus(
                id, OrderStatus.CANCELLED,
                reason != null ? reason : "Sipariş reddedildi");
        return ResponseEntity.ok(ApiResponse.ok("Sipariş reddedildi", response));
    }

    // Baskıya gönder
    @PostMapping("/orders/{id}/print")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> sendToPrint(
            @PathVariable UUID id) {
        OrderDetailResponse response = operatorService.updateStatus(
                id, OrderStatus.PRINTING, "Baskıya gönderildi");
        return ResponseEntity.ok(ApiResponse.ok("Baskıya gönderildi", response));
    }

    // Kargoya ver
    @PostMapping("/orders/{id}/ship")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> ship(
            @PathVariable UUID id,
            @RequestParam(required = false) String trackingNumber) {
        String note = trackingNumber != null
                ? "Kargoya verildi. Takip no: " + trackingNumber
                : "Kargoya verildi";
        OrderDetailResponse response = operatorService.updateStatus(
                id, OrderStatus.SHIPPED, note);
        return ResponseEntity.ok(ApiResponse.ok("Kargoya verildi", response));
    }

    // Tamamla
    @PostMapping("/orders/{id}/complete")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> complete(
            @PathVariable UUID id) {
        OrderDetailResponse response = operatorService.updateStatus(
                id, OrderStatus.COMPLETED, "Sipariş tamamlandı");
        return ResponseEntity.ok(ApiResponse.ok("Sipariş tamamlandı", response));
    }
}