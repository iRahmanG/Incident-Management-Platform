package com.maksud.incident.auth_service.token;

import com.maksud.incident.auth_service.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.maksud.incident.auth_service.token.entity.RefreshToken;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByUser(User user);
}
