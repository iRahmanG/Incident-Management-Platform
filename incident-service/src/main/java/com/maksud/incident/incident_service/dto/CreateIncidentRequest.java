package com.maksud.incident.incident_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateIncidentRequest {
    @NotBlank
    String title;

    @NotBlank
    String description;

    String source;
}
