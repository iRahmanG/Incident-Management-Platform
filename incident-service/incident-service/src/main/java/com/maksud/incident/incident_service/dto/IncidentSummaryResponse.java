package com.maksud.incident.incident_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record IncidentSummaryResponse(
        UUID id,
        String title,
        String severity,
        String status,
        UUID assignedTo,
        LocalDateTime createdAt
) {}
