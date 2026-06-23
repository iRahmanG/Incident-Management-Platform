package com.maksud.incident.incident_service.mapper;

import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.entity.Incident;
import org.springframework.stereotype.Component;

@Component
public class IncidentMapper {
    public IncidentResponse toResponse(Incident incident){
        return IncidentResponse.builder()
                .id(incident.getId())
                .title(incident.getTitle())
                .description(incident.getDescription())
                .source(
                        incident.getSource()!= null
                        ? incident.getSource().name() : null
                )
                .severity(incident.getIncidentSeverity().name())
                .status(incident.getStatus().name())
                .createdBy(incident.getCreatedBy())
                .assignedTo(incident.getAssignedTo())
                .createdAt(incident.getCreatedAt())
                .updatedAt(incident.getUpdatedAt())
                .build();
    }
}
