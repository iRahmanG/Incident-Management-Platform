package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.dto.IncidentSummaryResponse;
import com.maksud.incident.incident_service.repository.IncidentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request, UUID createdBy);

    IncidentResponse assignIncident(UUID incidentId, UUID userId);

    IncidentResponse acknowledgeIncident(UUID incidentId, UUID currentUserId) throws AccessDeniedException;

    IncidentResponse resolveIncident(UUID incidentId, UUID currentUserId, String resolutionSummary) throws AccessDeniedException;

    IncidentResponse closeIncident(UUID incidentId, String closureSummary);

    IncidentResponse reopenIncident(UUID incidentId, String reopenSummary);

    Page<IncidentSummaryResponse> getAllIncidents(int page, int size);

    IncidentResponse getIncidentById(UUID incidentId);
}
