package com.maksud.incident.api_gateway.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorResponse (
    LocalDateTime timestamp,
    int status,
    String error,
    String message
    ) { }
