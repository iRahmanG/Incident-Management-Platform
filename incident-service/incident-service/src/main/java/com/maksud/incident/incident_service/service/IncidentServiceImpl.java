package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.entity.Incident;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentSource;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import com.maksud.incident.incident_service.exception.IncidentNotFoundException;
import com.maksud.incident.incident_service.mapper.IncidentMapper;
import com.maksud.incident.incident_service.repository.IncidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IncidentServiceImpl implements IncidentService{

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;

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
}
