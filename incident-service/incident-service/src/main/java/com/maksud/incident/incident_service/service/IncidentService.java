package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request, UUID createdBy);
    IncidentResponse assignIncident(UUID incidentID, UUID userId);
}
