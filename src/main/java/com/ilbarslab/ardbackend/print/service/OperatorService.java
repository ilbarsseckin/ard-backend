package com.ilbarslab.ardbackend.print.service;

import com.ilbarslab.ardbackend.print.dto.response.OrderDetailResponse;
import com.ilbarslab.ardbackend.print.dto.response.OrderItemDetailResponse;
import com.ilbarslab.ardbackend.print.entity.Order;
import com.ilbarslab.ardbackend.print.entity.OrderItem;
import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import com.ilbarslab.ardbackend.print.repository.FileEntityRepository;
import com.ilbarslab.ardbackend.print.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperatorService {

    private final OrderRepository orderRepository;
    private final OrderTrackingService orderTrackingService;
    private final NotificationService notificationService;
    private final FileEntityRepository fileEntityRepository;

    // Tüm siparişleri getir
    public List<OrderDetailResponse> getAllOrders(OrderStatus status) {
        List<Order> orders;
        if (status != null) {
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(status);
        } else {
            orders = orderRepository.findAllByOrderByCreatedAtDesc();
        }
        return orders.stream().map(this::toDetailResponse).toList();
    }


    private OrderDetailResponse toDetailResponse(Order order) {
        boolean pageWarning = order.getPdfPageCount() != null &&
                order.getDeclaredPrints() != null &&
                order.getPdfPageCount() > order.getDeclaredPrints();

        List<OrderItemDetailResponse> itemResponses = order.getItems() != null
                ? order.getItems().stream().map(this::toItemDetailResponse).toList()
                : List.of();

        return OrderDetailResponse.builder()
                .id(order.getId())
                .customerName(order.getUser().getName())
                .customerEmail(order.getUser().getEmail())
                .customerPhone(order.getUser().getPhone())
                .status(order.getStatus())
                .totalPrice(order.getTotalPrice())
                .shippingAddress(order.getShippingAddress())
                .pdfPageCount(order.getPdfPageCount())
                .declaredPrints(order.getDeclaredPrints())
                .pageWarning(pageWarning)
                .createdAt(order.getCreatedAt())
                .items(itemResponses)
                .build();
    }

    private OrderItemDetailResponse toItemDetailResponse(OrderItem item) {
        var file = fileEntityRepository.findByOrderItemId(item.getId()).orElse(null);

        return OrderItemDetailResponse.builder()
                .id(item.getId())
                .productType(item.getProductType())
                .widthCm(item.getWidthCm())
                .heightCm(item.getHeightCm())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getUnitPrice().multiply(
                        java.math.BigDecimal.valueOf(item.getQuantity())))
                .fileS3Key(file != null ? file.getS3Key() : null)
                .fileOriginalName(file != null ? file.getOriginalName() : null)
                .filePageCount(file != null ? file.getPageCount() : null)
                .fileStatus(file != null ? file.getStatus().name() : null)
                .hasFile(file != null)
                .build();
    }

    public OrderDetailResponse getOrderDetail(UUID orderId) {
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));
        return toDetailResponse(order);
    }

    @Transactional
    public OrderDetailResponse updateStatus(UUID orderId, OrderStatus newStatus, String note) {
        orderTrackingService.updateStatus(orderId, newStatus, note);
        Order order = orderRepository.findByIdWithItems(orderId)
                .orElseThrow(() -> new RuntimeException("Sipariş bulunamadı"));

        try {
            notificationService.sendStatusUpdate(order);
        } catch (Exception e) {
            log.error("Bildirim gönderilemedi: {}", e.getMessage());
        }

        return toDetailResponse(order);
    }
}