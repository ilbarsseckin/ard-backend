package com.ilbarslab.ardbackend.print.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDetailResponse {
    private UUID id;
    private String productType;
    private Integer widthCm;
    private Integer heightCm;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String fileS3Key;
    private String fileOriginalName;
    private Integer filePageCount;
    private String fileStatus;
    private boolean hasFile;
}