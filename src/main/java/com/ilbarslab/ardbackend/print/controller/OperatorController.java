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

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<OrderDetailResponse>>> getAllOrders(
            @RequestParam(required = false) OrderStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(operatorService.getAllOrders(status)));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok(operatorService.getOrderDetail(id)));
    }

    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> updateStatus(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Durum güncellendi",
                operatorService.updateStatus(id, request.getStatus(), request.getNote())));
    }

    @PostMapping("/orders/{id}/approve")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> approve(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Sipariş onaylandı",
                operatorService.updateStatus(id, OrderStatus.REVIEWING, "Sipariş onaylandı")));
    }

    @PostMapping("/orders/{id}/reject")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> reject(
            @PathVariable UUID id,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.ok("Sipariş reddedildi",
                operatorService.updateStatus(id, OrderStatus.CANCELLED,
                        reason != null ? reason : "Sipariş reddedildi")));
    }

    @PostMapping("/orders/{id}/print")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> sendToPrint(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Baskıya gönderildi",
                operatorService.updateStatus(id, OrderStatus.PRINTING, "Baskıya gönderildi")));
    }

    @PostMapping("/orders/{id}/ship")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> ship(
            @PathVariable UUID id,
            @RequestParam(required = false) String trackingNumber) {
        String note = trackingNumber != null ? "Kargoya verildi. Takip no: " + trackingNumber : "Kargoya verildi";
        return ResponseEntity.ok(ApiResponse.ok("Kargoya verildi",
                operatorService.updateStatus(id, OrderStatus.SHIPPED, note)));
    }

    @PostMapping("/orders/{id}/complete")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> complete(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.ok("Sipariş tamamlandı",
                operatorService.updateStatus(id, OrderStatus.COMPLETED, "Sipariş tamamlandı")));
    }
}