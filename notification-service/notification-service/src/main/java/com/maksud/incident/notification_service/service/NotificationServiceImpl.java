package com.maksud.incident.notification_service.service;

import com.maksud.incident.notification_service.dto.NotificationResponse;
import com.maksud.incident.notification_service.dto.NotificationSummaryResponse;
import com.maksud.incident.notification_service.entity.Notification;
import com.maksud.incident.notification_service.entity.NotificationEventType;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import com.maksud.incident.notification_service.exception.NotificationNotFoundException;
import com.maksud.incident.notification_service.mapper.NotificationMapper;
import com.maksud.incident.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public Page<NotificationSummaryResponse> getAllNotifications(int page, int size, NotificationStatus status, NotificationEventType type) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        // no filters
        if(status == null && type == null){
            return notificationRepository.findAll(pageable)
                    .map(notificationMapper::toSummaryResponse);
        }
        // by status
        if(status !=null && type == null){
            return  notificationRepository.findByStatus(status, pageable)
                    .map(notificationMapper::toSummaryResponse);
        }
        // event type
        if(status == null) {
            return notificationRepository.findByEventType(type, pageable)
                    .map(notificationMapper::toSummaryResponse);
        }
        // status + event type
        return notificationRepository.findByStatusAndEventType(status, type, pageable)
                .map(notificationMapper::toSummaryResponse);

    }

    @Override
    public NotificationResponse getNotificationById(UUID id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow( () ->
                        new NotificationNotFoundException("Notification not found with id: " + id)
                );
        return notificationMapper.toResponse(notification);
    }
}
