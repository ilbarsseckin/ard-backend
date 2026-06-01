package com.ilbarslab.ardbackend.print.entity;

import com.ilbarslab.ardbackend.print.entity.enums.DealerStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "dealers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Dealer {
    private String estimatedMonthlyRevenue;
    private String businessType;
    private String website;
    private String note;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    // Firma bilgileri
    @Column(nullable = false)
    private String companyName;

    @Column(nullable = false, unique = true)
    private String taxNumber;        // Vergi no

    private String taxOffice;        // Vergi dairesi

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;          // İş adresi

    private String city;
    private String district;

    // Bayi ayarları
    @Column(precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal discountRate = BigDecimal.ZERO;  // İskonto %

    @Column(precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.ZERO;   // Kredi limiti

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DealerStatus status = DealerStatus.PENDING; // PENDING, APPROVED, REJECTED

    private String notes;            // Admin notları
    private String rejectionReason;  // Red sebebi

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}