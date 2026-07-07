package com.maksud.incident.notification_service.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationFailedEvent {

    private UUID notificationId;

    private UUID incidentId;

    private String recipientEmail;

    private String subject;

    private String message;

    private String errorMessage;

    private Integer retryCount;

    private LocalDateTime failedAt;
}
