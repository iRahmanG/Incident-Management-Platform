package com.maksud.incident.auth_service.token.dto;

import lombok.Builder;

@Builder
public record RefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) { }
