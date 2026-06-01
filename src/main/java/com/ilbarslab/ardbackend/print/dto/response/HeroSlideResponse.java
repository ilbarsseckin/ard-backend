package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HeroSlideResponse {
    private UUID id;
    private String label;
    private String title;
    private String description;
    private String ctaText;
    private String ctaLink;
    private String imageUrl;
    private String mobileImageUrl;
    private String backgroundColor;
    private String layout;
    private Integer sortOrder;
    private Boolean active;
    private Instant startsAt;
    private Instant endsAt;
    private Instant createdAt;
    private Instant updatedAt;
}
