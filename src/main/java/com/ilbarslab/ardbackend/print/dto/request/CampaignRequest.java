package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class CampaignRequest {
    private String label;
    private String title;
    private String description;
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
}
