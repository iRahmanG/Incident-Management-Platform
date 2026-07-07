package com.maksud.incident.incident_service.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class IncidentComment {
    private UUID id;
    private UUID incidentId;
    private UUID authorId;
    private String message;
    private LocalDateTime createdAt;
}
