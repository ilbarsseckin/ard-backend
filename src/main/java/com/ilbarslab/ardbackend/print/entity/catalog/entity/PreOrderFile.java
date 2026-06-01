package com.ilbarslab.ardbackend.print.entity.catalog.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.domain.Persistable;

import java.time.Instant;
import java.util.UUID;

/**
 * Sipariş öncesi yüklenen tasarım dosyası.
 * Persistable implement edilmiş — manuel set edilen UUID'lerde Spring Data JPA
 * UPDATE değil INSERT yapsın diye.
 */
@Entity
@Table(name = "pre_order_files")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreOrderFile implements Persistable<UUID> {

    @Id
    private UUID id;

    @Column(nullable = false, length = 500)
    private String originalName;

    @Column(nullable = false, length = 500)
    private String storedPath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(length = 100)
    private String mimeType;

    @Column
    private UUID claimedByOrderId;

    @Column
    private Instant claimedAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * Yeni mi yoksa veritabanından mı yüklendi?
     * Yeni ise persist() (INSERT), değilse merge() (UPDATE).
     */
    @Transient
    @Builder.Default
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public UUID getId() {
        return id;
    }

    /** DB'den yüklenince veya kayıt sonrası "artık yeni değil" olarak işaretle. */
    @PostLoad
    @PostPersist
    private void markNotNew() {
        this.isNew = false;
    }
}