package com.maksud.incident.auth_service.token.entity;

import com.maksud.incident.auth_service.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    private String deviceName;
    private String userAgent;
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private LocalDateTime lastUsedAt;

}
