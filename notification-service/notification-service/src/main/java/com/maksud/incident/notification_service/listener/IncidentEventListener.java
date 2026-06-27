package com.maksud.incident.notification_service.listener;

import com.maksud.incident.notification_service.event.IncidentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class IncidentEventListener {

    @KafkaListener(
            topics = "incident-events",
            groupId = "notification-group"
    )
    public void consume(IncidentEvent event){
        log.info("Received Incident Event");

        log.info("Type       : {}", event.getEventType());
        log.info("IncidentId : {}", event.getIncidentId());
        log.info("Title      : {}", event.getTitle());
        log.info("Severity   : {}", event.getSeverity());
    }
}
