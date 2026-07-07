package com.maksud.incident.incident_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record IncidentEvent(
        String eventType,
        UUID incidentId,
        String title,
        String status,
        String severity,
        UUID createdBy,
        UUID assignedTo,
        LocalDateTime occurredAt
) {
}
