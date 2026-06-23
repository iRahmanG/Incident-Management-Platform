package com.maksud.incident.incident_service.dto;

import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentSource;
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
        LocalDateTime updatedAt
) {}