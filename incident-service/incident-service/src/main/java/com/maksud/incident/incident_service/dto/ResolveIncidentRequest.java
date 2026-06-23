package com.maksud.incident.incident_service.dto;

import jakarta.validation.constraints.NotBlank;

public record ResolveIncidentRequest(
        @NotBlank
        String resolutionSummary
) {}
