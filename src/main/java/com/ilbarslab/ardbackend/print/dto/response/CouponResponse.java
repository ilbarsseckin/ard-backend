package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CouponResponse {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String type;
    private BigDecimal discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal giftAmount;
    private BigDecimal minOrderAmount;
    private Integer maxUsage;
    private Integer currentUsage;
    private Integer perUserLimit;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean active;
    private Boolean autoIssueOnFirstVisit;
    private BigDecimal autoIssueOnOrderAmount;
    private LocalDateTime createdAt;
}
