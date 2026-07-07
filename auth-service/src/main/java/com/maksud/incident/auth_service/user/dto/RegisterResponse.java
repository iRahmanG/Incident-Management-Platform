package com.maksud.incident.auth_service.user.dto;

import com.maksud.incident.auth_service.user.entity.Role;

import java.util.UUID;

public record RegisterResponse(
        UUID userId,
        String name,
        String email,
        Role role
) {}