package com.ilbarslab.ardbackend.print.dto.request;

import lombok.Data;

import java.time.Instant;

@Data
public class HeroSlideRequest {
    private String label;
    private String title;
    private String description;
    private String ctaText;
    private String ctaLink;
    private String imageUrl;
    private String mobileImageUrl;
    private String backgroundColor;
    private String layout;        // SPLIT_LEFT | SPLIT_RIGHT | OVERLAY | IMAGE_ONLY
    private Integer sortOrder;
    private Boolean active;
    private Instant startsAt;
    private Instant endsAt;
}
