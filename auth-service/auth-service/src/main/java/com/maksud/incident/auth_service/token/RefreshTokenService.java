package com.maksud.incident.auth_service.token;

import com.maksud.incident.auth_service.config.JwtProperties;
import com.maksud.incident.auth_service.token.entity.RefreshToken;
import com.maksud.incident.auth_service.user.entity.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private String generateRefreshToken(){
        byte[] bytes = new byte[32];
        new java.security.SecureRandom()
                .nextBytes(bytes);
        return java.util.Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);

    }

    private String hashToken(String token){
        try{
            java.security.MessageDigest digest = java.security.MessageDigest.
                    getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());

            return java.util.HexFormat
                    .of()
                    .formatHex(hash);
        }catch(Exception e) {
            throw new RuntimeException("Unable to hash refresh token: ", e);
        }
    }

    @Transactional
    public String createRefreshToken(User user, String deviceName, String userAgent, String ipAddress){
        String plainToken  = generateRefreshToken();
        String tokenHash = hashToken(plainToken);
        LocalDateTime now  = LocalDateTime.now();
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .tokenHash(tokenHash)
                .deviceName(deviceName)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .createdAt(now)
                .lastUsedAt(now)
                .expiresAt(now.plus(Duration.ofMillis(
                        jwtProperties.getRefreshTokenExpiration())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(refreshToken);
        return plainToken;
    }

    @Transactional
    public RefreshToken validateRefreshToken(String plainToken){
        String tokenHash = hashToken(plainToken);
        RefreshToken refreshToken = refreshTokenRepository
                .findByTokenHash(tokenHash)
                .orElseThrow(()-> new RuntimeException("Invalid Refresh Token"));

        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh Token Revoked");
        }
        if(refreshToken.getExpiresAt().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Refresh Token Expired");
        }
        refreshToken.setLastUsedAt(LocalDateTime.now());
        return refreshToken;
    }

    @Transactional
    public void revokeToken(RefreshToken refreshToken){
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
    @Transactional
    public void revokeAllUserTokens(User user){
        List<RefreshToken> tokens = refreshTokenRepository.findAllByUser(user);
        tokens.forEach( token -> token.setRevoked(true));
        refreshTokenRepository.saveAll(tokens);
    }

    @Transactional
    public String rotateRefreshToken(RefreshToken oldToken){
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        String newPlainToken = generateRefreshToken();
        String newTokenHash = hashToken(newPlainToken);
        LocalDateTime now = LocalDateTime.now();

        RefreshToken newToken = RefreshToken.builder()
                .user(oldToken.getUser())
                .tokenHash(newTokenHash)
                .deviceName(oldToken.getDeviceName())
                .userAgent(oldToken.getUserAgent())
                .ipAddress(oldToken.getIpAddress())
                .createdAt(now)
                .lastUsedAt(now)
                .expiresAt(now.plus(Duration.ofMillis(jwtProperties.getRefreshTokenExpiration())))
                .revoked(false)
                .build();
        refreshTokenRepository.save(newToken);
        return newPlainToken;
    }

}
