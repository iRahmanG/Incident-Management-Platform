package com.maksud.incident.notification_service.repository;

import com.maksud.incident.notification_service.entity.Notification;
import com.maksud.incident.notification_service.entity.NotificationEventType;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByStatus(NotificationStatus status, Pageable pageable);

    Page<Notification> findByEventType(NotificationEventType type, Pageable pageable);

    Page<Notification> findByStatusAndEventType(NotificationStatus status, NotificationEventType type, Pageable pageable);

    List<Notification> findByStatus(NotificationStatus status);
}
