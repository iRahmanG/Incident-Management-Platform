package com.maksud.incident.incident_service.security;

import lombok.Builder;

import java.util.UUID;

@Builder
public record AuthenticatedUser(
        UUID userId,
        String email,
        String role
) {
}
