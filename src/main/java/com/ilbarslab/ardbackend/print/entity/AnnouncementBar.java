package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "announcement_bars")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AnnouncementBar {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 200)
    private String message;

    @Column(length = 200)
    private String subMessage;

    @Column(length = 50)
    private String couponCode;

    @Column(name = "bg_color", length = 30)
    @Builder.Default
    private String bgColor = "#F4821F";

    @Column(name = "text_color", length = 30)
    @Builder.Default
    private String textColor = "#FFFFFF";

    @Column(name = "ends_at")
    private Instant endsAt;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;
}
