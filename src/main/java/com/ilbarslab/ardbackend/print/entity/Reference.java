package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "brand_references")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String sector;

    @Column(nullable = false)
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    // R2'ye yüklenen logo URL'i
    private String logoUrl;

    // Logo yoksa gösterilecek renk (#E31E24 gibi)
    @Builder.Default
    private String color = "#F4821F";

    // Logo yoksa gösterilecek kısaltma (M, EP gibi)
    private String abbr;

    @Builder.Default
    private Boolean featured = false;

    @Builder.Default
    private Boolean active = true;

    @Builder.Default
    private Integer displayOrder = 0;
}