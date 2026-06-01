package com.ilbarslab.ardbackend.print.controller;

import com.ilbarslab.ardbackend.print.dto.response.ApiResponse;
import com.ilbarslab.ardbackend.print.dto.response.OrderDetailResponse;
import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.OrderStatusHistory;
import com.ilbarslab.ardbackend.print.service.OrderService;
import com.ilbarslab.ardbackend.print.service.OrderTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderTrackingService orderTrackingService;

    /** Sepetten sipariş oluştur — frontend orderId + totalPrice + status bekliyor */
    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkout(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam UUID addressId) {
        Order order = orderService.createFromCart(userDetails.getUsername(), addressId);
        return ResponseEntity.ok(ApiResponse.ok("Sipariş oluşturuldu",
                Map.of(
                        "orderId", order.getId(),
                        "totalPrice", order.getTotalPrice(),
                        "status", order.getStatus()
                )));
    }

    /** Müşterinin sipariş geçmişi — DTO listesi */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderDetailResponse>>> getMyOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<OrderDetailResponse> orders = orderService.getUserOrders(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(orders));
    }

    /** Sipariş detayı — DTO, lazy hatası yok */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderDetailResponse>> getOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        OrderDetailResponse order = orderService.getOrder(userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(order));
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<ApiResponse<List<OrderStatusHistory>>> getHistory(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {
        List<OrderStatusHistory> history = orderTrackingService.getHistory(
                userDetails.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok(history));
    }
}