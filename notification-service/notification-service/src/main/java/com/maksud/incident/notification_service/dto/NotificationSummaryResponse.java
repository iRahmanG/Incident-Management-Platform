package com.maksud.incident.notification_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationSummaryResponse(
        UUID id,
        UUID incidentId,
        String recipientEmail,
        String eventType,
        String status,
        LocalDateTime createdAt
) {
}
