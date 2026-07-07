package com.maksud.incident.notification_service.listener;

import com.maksud.incident.notification_service.event.IncidentEvent;
import com.maksud.incident.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncidentEventListener {

    private final NotificationService notificationService;

    @KafkaListener(
            topics = "incident-events",
            groupId = "notification-group"
    )
    public void consume(IncidentEvent event){
        log.info("Received Incident Event");

        notificationService.saveNotification(event);
        log.info("Notification stored successfully");
    }
}
