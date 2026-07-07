package com.maksud.incident.notification_service.service;

import com.maksud.incident.notification_service.dto.NotificationResponse;
import com.maksud.incident.notification_service.dto.NotificationSummaryResponse;
import com.maksud.incident.notification_service.entity.Notification;
import com.maksud.incident.notification_service.entity.NotificationEventType;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import com.maksud.incident.notification_service.event.IncidentEvent;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface NotificationService {

    Page<NotificationSummaryResponse> getAllNotifications(
            int page,
            int size,
            NotificationStatus status,
            NotificationEventType type
    );

    NotificationResponse getNotificationById(UUID id);

    Notification saveNotification(IncidentEvent event);
}
