package com.maksud.incident.auth_service.security;

import com.maksud.incident.auth_service.user.entity.Role;

import java.util.UUID;

public record UserPrincipal(
        UUID userId,
        String email,
        Role role
) {
}
