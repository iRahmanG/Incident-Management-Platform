package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.IncidentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaTemplate<String, IncidentEvent> kafkaTemplate;

    public void publish(IncidentEvent event) {

        kafkaTemplate.send(
                "incident-events",
                event.incidentId().toString(),
                event
        ).whenComplete((result, ex) -> {

            if (ex != null) {
                log.error("Kafka publish failed", ex);
            } else {
                log.info(
                        "Published to topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }

        });
    }
}
