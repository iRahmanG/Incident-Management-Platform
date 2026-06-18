package com.maksud.incident.auth_service.auth.service;

import com.maksud.incident.auth_service.auth.dto.AuthResponse;
import com.maksud.incident.auth_service.auth.dto.LoginRequest;
import com.maksud.incident.auth_service.auth.dto.LogoutRequest;
import com.maksud.incident.auth_service.security.JwtService;
import com.maksud.incident.auth_service.token.RefreshTokenRepository;
import com.maksud.incident.auth_service.token.RefreshTokenService;
import com.maksud.incident.auth_service.token.dto.RefreshRequest;
import com.maksud.incident.auth_service.token.dto.RefreshResponse;
import com.maksud.incident.auth_service.token.entity.RefreshToken;
import com.maksud.incident.auth_service.user.entity.User;
import com.maksud.incident.auth_service.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RefreshTokenService refreshTokenService;

    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid Credentials"));

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())){
            throw new BadCredentialsException("Invalid Credentials");
        }
        String deviceName = "Unknown Device";
        String userAgent = httpRequest.getHeader("User-Agent");
        String ipAddress = httpRequest.getRemoteAddr();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user,deviceName, userAgent, ipAddress);
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    public RefreshResponse refresh(RefreshRequest request){
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.getRefreshToken());

        User user = refreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken);

        return RefreshResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    public void logout(LogoutRequest logoutRequest){
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(logoutRequest.getRefreshToken());
        refreshTokenService.revokeToken(refreshToken);
    }

    public void logoutAll(LogoutRequest logoutRequest){
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(logoutRequest.getRefreshToken());
        refreshTokenService.revokeAllUserTokens(refreshToken.getUser());
    }
}
