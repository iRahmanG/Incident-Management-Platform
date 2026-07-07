package com.maksud.incident.notification_service.mapper;

import com.maksud.incident.notification_service.dto.NotificationResponse;
import com.maksud.incident.notification_service.dto.NotificationSummaryResponse;
import com.maksud.incident.notification_service.entity.Notification;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .incidentId(notification.getIncidentId())
                .recipientEmail(notification.getRecipientEmail())
                .eventType(notification.getEventType().name())
                .status(notification.getStatus().name())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .retryCount(notification.getRetryCount())
                .errorMessage(notification.getErrorMessage())
                .createdAt(notification.getCreatedAt())
                .sentAt(notification.getSentAt())
                .build();
    }

    public NotificationSummaryResponse toSummaryResponse(Notification notification) {
        return NotificationSummaryResponse.builder()
                .id(notification.getId())
                .incidentId(notification.getIncidentId())
                .recipientEmail(notification.getRecipientEmail())
                .eventType(notification.getEventType().name())
                .status(notification.getStatus().name())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
