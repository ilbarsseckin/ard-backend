package com.ilbarslab.ardbackend.print.entity;

import com.ilbarslab.ardbackend.print.entity.catalog.entity.CatalogOrder;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "catalog_order_files", indexes = {
    @Index(name = "idx_catfile_order",   columnList = "order_id"),
    @Index(name = "idx_catfile_created", columnList = "created_at"),
})
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CatalogOrderFile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private CatalogOrder order;

    /** Müşterinin dosyasının orijinal adı — "kartvizit-tasarim.pdf" */
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    /** Disk'te saklanan benzersiz ad — "abc-uuid.pdf" */
    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    /** Tam disk yolu (upload-dir'e göre relative) — "customer-designs/CAT-ABC/uuid.pdf" */
    @Column(name = "storage_path", nullable = false, length = 500)
    private String storagePath;

    /** Boyut (byte) */
    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    /** MIME tipi — application/pdf, image/jpeg vb. */
    @Column(name = "mime_type", length = 100)
    private String mimeType;

    /** PDF için sayfa sayısı; resim için null veya 1 */
    @Column(name = "page_count")
    private Integer pageCount;

    /** PDF sayfa sayısı tier qty'sinden farklı ise admin'e uyarı için flag */
    @Column(name = "page_warning")
    @Builder.Default
    private Boolean pageWarning = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
