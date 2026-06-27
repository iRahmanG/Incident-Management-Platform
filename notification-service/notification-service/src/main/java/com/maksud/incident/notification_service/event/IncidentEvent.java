package com.maksud.incident.notification_service.event;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentEvent {

    private String eventType;

    private UUID incidentId;

    private String title;

    private String status;

    private String severity;

    private UUID createdBy;

    private UUID assignedTo;

    private LocalDateTime occurredAt;
}
