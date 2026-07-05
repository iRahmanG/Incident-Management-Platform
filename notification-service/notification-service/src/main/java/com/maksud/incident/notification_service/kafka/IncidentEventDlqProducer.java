package com.maksud.incident.notification_service.kafka;

import com.maksud.incident.notification_service.constants.NotificationConstants;
import com.maksud.incident.notification_service.event.IncidentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentEventDlqProducer {

    private final KafkaTemplate<String, IncidentEvent> kafkaTemplate;

    public void publish(IncidentEvent event){
        kafkaTemplate.send(
                NotificationConstants.INCIDENT_EVENTS_DLQ_TOPIC,
                event.getIncidentId().toString(),
                event
        );

        log.warn("Incident {} published to DLQ", event.getIncidentId());
    }
}
