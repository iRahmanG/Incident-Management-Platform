package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.dto.IncidentSummaryResponse;
import com.maksud.incident.incident_service.entity.Incident;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

public interface IncidentService {

    IncidentResponse createIncident(CreateIncidentRequest request, UUID createdBy);

    IncidentResponse assignIncident(UUID incidentId, UUID userId);

    IncidentResponse acknowledgeIncident(UUID incidentId, UUID currentUserId) throws AccessDeniedException;

    IncidentResponse resolveIncident(UUID incidentId, UUID currentUserId, String resolutionSummary) throws AccessDeniedException;

    IncidentResponse closeIncident(UUID incidentId, String closureSummary);

    IncidentResponse reopenIncident(UUID incidentId, String reopenSummary);

    Page<IncidentSummaryResponse> getAllIncidents(int page, int size, IncidentStatus status, IncidentSeverity severity);

    IncidentResponse getIncidentById(UUID incidentId);

}
