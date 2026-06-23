package com.maksud.incident.incident_service.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AssignIncidentRequest(
        @NotNull
        UUID engineerId
) {
}