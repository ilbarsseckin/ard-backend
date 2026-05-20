package com.ilbarslab.ardbackend.print.entity;

import com.ilbarslab.ardbackend.print.entity.enums.FileStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private OrderItem orderItem;

    @Column(nullable = false)
    private String s3Key;

    @Column(nullable = false)
    private String originalName;

    @Builder.Default
    private Integer pageCount = 1;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private FileStatus status = FileStatus.LOCKED;

    @CreationTimestamp
    private LocalDateTime uploadedAt;
}
