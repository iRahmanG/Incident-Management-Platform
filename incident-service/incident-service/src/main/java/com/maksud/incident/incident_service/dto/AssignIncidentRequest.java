package com.maksud.incident.incident_service.dto;

import java.util.UUID;

public record AssignIncidentRequest(
        UUID engineerId
) {
}