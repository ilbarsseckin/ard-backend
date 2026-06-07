package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "campaigns", indexes = {
        @Index(name = "idx_campaign_active", columnList = "active"),
        @Index(name = "idx_campaign_sort",   columnList = "sort_order"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Campaign {

    @Id
    @GeneratedValue
    private UUID id;

    /** URL slug — örn. "3-yelken-bayrak-1-tl-kartvizit" → /lp/3-yelken-bayrak-1-tl-kartvizit */
    @Column(length = 200, unique = true)
    private String slug;

    /** Üst etiket — örn. "FIRSAT", "PAKET KAMPANYA" */
    @Column(length = 200)
    private String label;

    /** Başlık — örn. "3 Yelken Bayrak Alana Kartvizit 1 TL" */
    @Column(nullable = false, length = 200)
    private String title;

    /** Teklifin detay açıklaması (opsiyonel) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** Landing page için uzun içerik (JSON veya HTML) */
    @Column(name = "landing_content", columnDefinition = "TEXT")
    private String landingContent;

    /** Rozet metni — örn. "1 TL", "PAKET", "%50" */
    @Column(name = "badge_text", length = 40)
    private String badgeText;

    /** Rozet rengi — örn. "#F4821F" */
    @Column(name = "badge_color", length = 30)
    private String badgeColor;

    /** Desktop banner görseli */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /** Mobil için ayrı görsel — null ise imageUrl kullanılır */
    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    /** Tema/arka plan rengi */
    @Column(name = "background_color", length = 30)
    private String backgroundColor;

    /** CTA buton metni */
    @Column(name = "cta_text", length = 60)
    private String ctaText;

    /** CTA link */
    @Column(name = "cta_link", length = 500)
    private String ctaLink;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "starts_at")
    private Instant startsAt;

    @Column(name = "ends_at")
    private Instant endsAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
