package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogOrderResponse {
    private UUID id;
    private String orderNumber;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String customerAddress;
    private String city;
    private String district;
    private String notes;
    private BigDecimal subtotalUsd;
    private BigDecimal subtotalTl;
    private BigDecimal discountAmountTl;
    private String couponCode;
    private BigDecimal totalTl;
    private BigDecimal usdKurAtOrder;
    private String status;
    private String paymentStatus;
    private UUID userId;
    private List<CatalogOrderItemResponse> items;
    private Instant createdAt;
    private Instant updatedAt;
    @Builder.Default
    private Boolean guestAccountCreated = false;
    private String trackingNumber;
    private String cargoCompany;
}