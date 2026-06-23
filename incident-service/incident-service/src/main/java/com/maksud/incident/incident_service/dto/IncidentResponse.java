package com.maksud.incident.incident_service.dto;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record IncidentResponse(
        UUID id,
        String title,
        String description,
        String source,
        String severity,
        String status,
        UUID createdBy,
        UUID assignedTo,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,

        LocalDateTime resolvedAt,
        LocalDateTime closedAt,
        String resolutionSummary,
        String closureSummary,
        String reopenSummary
) {}