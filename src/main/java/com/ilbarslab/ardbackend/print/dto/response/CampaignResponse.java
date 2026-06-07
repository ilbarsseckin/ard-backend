package com.ilbarslab.ardbackend.print.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignResponse {
    private UUID id;
    private String slug;
    private String label;
    private String title;
    private String description;
    private String landingContent;
    private String badgeText;
    private String badgeColor;
    private String imageUrl;
    private String mobileImageUrl;
    private String backgroundColor;
    private String ctaText;
    private String ctaLink;
    private Integer sortOrder;
    private Boolean active;
    private Instant startsAt;
    private Instant endsAt;
    private Instant createdAt;
    private Instant updatedAt;
}
