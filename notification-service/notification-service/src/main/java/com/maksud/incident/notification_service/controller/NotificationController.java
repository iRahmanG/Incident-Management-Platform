package com.maksud.incident.notification_service.controller;

import com.maksud.incident.notification_service.dto.NotificationResponse;
import com.maksud.incident.notification_service.dto.NotificationSummaryResponse;
import com.maksud.incident.notification_service.entity.NotificationEventType;
import com.maksud.incident.notification_service.entity.NotificationStatus;
import com.maksud.incident.notification_service.service.NotificationService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponse> getNotificationById(@PathVariable UUID id) {
        NotificationResponse response = notificationService.getNotificationById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<NotificationSummaryResponse>> getAllNotification(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false)NotificationEventType eventType
    ){
        Page<NotificationSummaryResponse> notifications = notificationService
                .getAllNotifications(page, size, status, eventType);

        return ResponseEntity.ok(notifications);

    }

    @GetMapping("/test")
    public String test(){
        return "notification service working";
    }
}
