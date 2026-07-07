package com.maksud.incident.notification_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationResponse(
    UUID id,
    UUID incidentId,
    String recipientEmail,
    String eventType,
    String status,
    String subject,
    String message,
    Integer retryCount,
    String errorMessage,
    LocalDateTime createdAt,
    LocalDateTime sentAt
) {
}