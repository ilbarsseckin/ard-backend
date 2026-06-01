package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "hero_slides", indexes = {
    @Index(name = "idx_hero_active",    columnList = "active"),
    @Index(name = "idx_hero_sort",      columnList = "sort_order"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class HeroSlide {

    @Id
    @GeneratedValue
    private UUID id;

    /** Üst etiket — örn. "Reklamınız Kalıcı Olsun" */
    @Column(length = 200)
    private String label;

    /** Büyük başlık — örn. "Oto Kokusu" */
    @Column(nullable = false, length = 200)
    private String title;

    /** Açıklama / alt metin (opsiyonel) */
    @Column(columnDefinition = "TEXT")
    private String description;

    /** CTA buton metni — örn. "Tıkla", "Hemen Sipariş Ver" */
    @Column(name = "cta_text", length = 60)
    private String ctaText;

    /** CTA link — iç (/urun/oto-kokusu) veya dış URL */
    @Column(name = "cta_link", length = 500)
    private String ctaLink;

    /** Desktop için resim URL'i (full URL) */
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    /** Mobil için ayrı resim — null ise imageUrl kullanılır */
    @Column(name = "mobile_image_url", length = 500)
    private String mobileImageUrl;

    /** Tema rengi — örn. "#F4821F" veya "green" (CSS variable) */
    @Column(name = "background_color", length = 30)
    private String backgroundColor;

    /** Yazılar resmin üstüne mi gelsin yoksa yan tarafta mı */
    @Enumerated(EnumType.STRING)
    @Column(name = "layout", length = 20)
    @Builder.Default
    private HeroLayout layout = HeroLayout.SPLIT_LEFT;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Bu tarihten önce gösterme (null = sınırsız geçmiş) */
    @Column(name = "starts_at")
    private Instant startsAt;

    /** Bu tarihten sonra gösterme (null = sınırsız gelecek) */
    @Column(name = "ends_at")
    private Instant endsAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
