package com.maksud.incident.incident_service.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserContext {
    private final HttpServletRequest request;

    public AuthenticatedUser getCurrentUser(){
        return AuthenticatedUser.builder()
                .userId(UUID.fromString(request.getHeader("X-User-Id")))
                .email(request.getHeader("X-User-Email"))
                .role(request.getHeader("X-User-Role"))
                .build();
    }
}
