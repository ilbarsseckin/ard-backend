package com.ilbarslab.ardbackend.print.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_app_roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAppRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "app_role_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private AppRole appRole;

    @CreationTimestamp
    private LocalDateTime assignedAt;

    private String assignedBy;
}