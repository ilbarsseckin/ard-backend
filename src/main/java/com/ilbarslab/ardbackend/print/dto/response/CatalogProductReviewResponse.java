package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CatalogProductReviewResponse {
    private UUID id;
    private UUID productId;
    private Integer rating;
    private String comment;
    private String displayName;   // "Ali T." veya "Anonim Kullanıcı"
    private Boolean anonymous;
    private Boolean approved;     // sadece admin'e döner
    private Instant createdAt;
}
