package com.ilbarslab.ardbackend.print.dto.response;

import com.ilbarslab.ardbackend.print.entity.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailResponse {
    private UUID id;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private String shippingAddress;
    private Integer pdfPageCount;
    private Integer declaredPrints;
    private boolean pageWarning;
    private LocalDateTime createdAt;
    private List<OrderItemDetailResponse> items;
}