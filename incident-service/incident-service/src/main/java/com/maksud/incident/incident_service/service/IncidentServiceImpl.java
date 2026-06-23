package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.entity.Incident;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentSource;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import com.maksud.incident.incident_service.exception.IncidentNotAssignedException;
import com.maksud.incident.incident_service.exception.IncidentNotFoundException;
import com.maksud.incident.incident_service.mapper.IncidentMapper;
import com.maksud.incident.incident_service.repository.IncidentRepository;
import com.maksud.incident.incident_service.security.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService{

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;
    private final UserContext userContext;

    @Override
    public IncidentResponse createIncident(CreateIncidentRequest request, UUID createdBy) {
        Incident incident = Incident.builder()
                .id(UUID.randomUUID())
                .title(request.getTitle())
                .description(request.getDescription())
                .source(
                        request.getSource() != null
                        ? IncidentSource.valueOf(request.getSource()) : IncidentSource.MANUAL
                )
                .incidentSeverity(IncidentSeverity.P3)
                .status(IncidentStatus.OPEN)
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .updatedAt(LocalDateTime.now())
                .build();

        Incident saved = incidentRepository.save(incident);
        return incidentMapper.toResponse(saved);
    }

    @Override
    public IncidentResponse assignIncident(UUID incidentId, UUID engineerId){
        Incident incident = incidentRepository.findById(incidentId).orElseThrow(() ->
                new IncidentNotFoundException("Incident not found with id: "+ incidentId)
        );
        // reassign to same engineer
        if (engineerId.equals(incident.getAssignedTo())) {
            return incidentMapper.toResponse(incident);
        }

        Set<IncidentStatus> assignableStatuses = Set.of(
                IncidentStatus.OPEN,
                IncidentStatus.IN_PROGRESS,
                IncidentStatus.REOPENED
        );
        if (!assignableStatuses.contains(incident.getStatus())) {
            throw new IllegalStateException(
                    "Incident cannot be assigned in status: " + incident.getStatus()
            );
        }

        incident.setAssignedTo(engineerId);
        incident.setUpdatedAt(LocalDateTime.now());
        Incident saved =  incidentRepository.save(incident);

        return incidentMapper.toResponse(saved);
    }

    @Override
    public IncidentResponse acknowledgeIncident(UUID incidentId, UUID currentUserId) throws AccessDeniedException {
        Incident incident = incidentRepository.findById(incidentId).orElseThrow(() ->
                new IncidentNotFoundException("Incident not found with id: " + incidentId)
        );
        if(incident.getAssignedTo() == null){
            throw new IllegalStateException("Incident must be assigned before acknowledgement");
        }
        if(!incident.getAssignedTo().equals(currentUserId)){
            throw new AccessDeniedException("Only assigned engineer can acknowledge this incident");
        }
        if(incident.getStatus() != IncidentStatus.OPEN && incident.getStatus() != IncidentStatus.REOPENED){
            throw new IllegalStateException("Incident cannot be acknowledged in status " + incident.getStatus());
        }
        incident.setStatus(IncidentStatus.IN_PROGRESS);
        incident.setUpdatedAt(LocalDateTime.now());

        Incident saved = incidentRepository.save(incident);

        return incidentMapper.toResponse(saved);
    }

    @Override
    public IncidentResponse resolveIncident(UUID incidentId, UUID currentUserId, String resolutionSummary) throws AccessDeniedException {
        Incident incident  = incidentRepository.findById(incidentId).orElseThrow(() ->
                new IncidentNotFoundException("Incident not found with id " + incidentId)
        );
        if (incident.getAssignedTo() == null){
            throw new IllegalStateException("Incident must be assigned before resolving");
        }
        if(!incident.getAssignedTo().equals(currentUserId)){
            throw new AccessDeniedException("Only assigned engineer can resolve the incident");
        }
        if(!incident.getStatus().equals(IncidentStatus.IN_PROGRESS)){
            throw new IllegalStateException("Only IN_PROGRESS incidents can be resolved");
        }
        if(resolutionSummary == null || resolutionSummary.isBlank()){
            throw new IllegalArgumentException(
                    "Resolution summary is required"
            );
        }
        incident.setStatus(IncidentStatus.RESOLVED);
        incident.setResolvedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        incident.setResolutionSummary(resolutionSummary);

        Incident saved = incidentRepository.save(incident);
        return incidentMapper.toResponse(saved);
    }

}
