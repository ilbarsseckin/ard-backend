package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductReviewStatsResponse {
    private Double averageRating;        // 4.5
    private Long totalCount;             // 42
    private Map<Integer, Long> distribution;  // {1:0, 2:1, 3:5, 4:10, 5:25}
    private Boolean canUserReview;       // sadece auth çağrı için
    private Boolean userAlreadyReviewed; // sadece auth çağrı için
}
