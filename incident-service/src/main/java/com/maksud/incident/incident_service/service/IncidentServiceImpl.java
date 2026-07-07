package com.maksud.incident.incident_service.service;

import com.maksud.incident.incident_service.constants.CacheConstants;
import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentEvent;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.dto.IncidentSummaryResponse;
import com.maksud.incident.incident_service.entity.Incident;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentSource;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import com.maksud.incident.incident_service.exception.IncidentNotFoundException;
import com.maksud.incident.incident_service.mapper.IncidentMapper;
import com.maksud.incident.incident_service.repository.IncidentRepository;
import com.maksud.incident.incident_service.security.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncidentServiceImpl implements IncidentService{

    private final IncidentRepository incidentRepository;
    private final IncidentMapper incidentMapper;
    private final UserContext userContext;
    private final KafkaProducerService producer;
    private final RedisService redisService;

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
        producer.publish(
                IncidentEvent.builder()
                        .eventType("INCIDENT_CREATED")
                        .incidentId(saved.getId())
                        .title(saved.getTitle())
                        .status(saved.getStatus().name())
                        .severity(saved.getIncidentSeverity().name())
                        .createdBy(saved.getCreatedBy())
                        .assignedTo(saved.getAssignedTo())
                        .occurredAt(saved.getCreatedAt())
                        .build()

        );
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

    @Override
    public IncidentResponse closeIncident(UUID incidentId, String closureSummary) {
        Incident incident = incidentRepository.findById(incidentId).orElseThrow(() ->
                new IncidentNotFoundException("Incident not found with id: " + incidentId)
        );
        if(incident.getStatus() != IncidentStatus.RESOLVED) {
            throw new IllegalStateException("Only RESOLVED incident can be closed");
        }
        if(closureSummary == null || closureSummary.isBlank()){
            throw new IllegalArgumentException("Closure summary is required");
        }
        incident.setStatus(IncidentStatus.CLOSED);
        incident.setClosureSummary(closureSummary);
        incident.setAssignedTo(null);
        incident.setClosedAt(LocalDateTime.now());
        incident.setUpdatedAt(LocalDateTime.now());
        Incident saved = incidentRepository.save(incident);
        return incidentMapper.toResponse(saved);
    }

    @Override
    public IncidentResponse reopenIncident(UUID incidentId, String reopenSummary) {
        Incident incident = incidentRepository.findById(incidentId).orElseThrow(() ->
                new IncidentNotFoundException("Incident not found with id: " + incidentId)
                );

        if(incident.getStatus() != IncidentStatus.RESOLVED && incident.getStatus() != IncidentStatus.CLOSED) {
            throw new IllegalStateException("Only RESOLVED or CLOSED incidents can be reopened");
        }
        if(reopenSummary == null || reopenSummary.isBlank()){
            throw  new IllegalArgumentException("Reopen summary is required");
        }
        incident.setStatus(IncidentStatus.REOPENED);
        incident.setReopenSummary(reopenSummary);
        incident.setAssignedTo(null);
        incident.setUpdatedAt(LocalDateTime.now());

        Incident saved = incidentRepository.save(incident);

        redisService.delete(
                CacheConstants.INCIDENT_CACHE_PREFIX + incident.getId()
        );
        return incidentMapper.toResponse(saved);
    }

    @Override
    public Page<IncidentSummaryResponse> getAllIncidents(int page, int size, IncidentStatus status, IncidentSeverity severity) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("createdAt").descending()
        );
        // No filters
        if(status == null && severity == null){
            return incidentRepository.findAll(pageable)
                    .map(incidentMapper::toSummaryResponse);
        }
        // Status only
        if(status!=null && severity == null){
            return incidentRepository.findByStatus(status, pageable)
                    .map(incidentMapper::toSummaryResponse);
        }
        // Severity Only
        if (status == null) {
            return incidentRepository.findByIncidentSeverity(severity, pageable)
                    .map(incidentMapper::toSummaryResponse);
        }
        // Status + Severity
        return incidentRepository
                .findByStatusAndIncidentSeverity(status, severity, pageable)
                .map(incidentMapper::toSummaryResponse);

    }

    @Override
    public IncidentResponse getIncidentById(UUID id) {

        String cacheKey =
                CacheConstants.INCIDENT_CACHE_PREFIX + id;

        var cachedIncident =
                redisService.get(cacheKey, IncidentResponse.class);

        if (cachedIncident.isPresent()) {
            log.info("Fetching incident {} from Redis", id);
            return cachedIncident.get();
        }

        log.info("Fetching incident {} from database", id);

        Incident incident =
                incidentRepository.findById(id)
                        .orElseThrow(() ->
                                new IncidentNotFoundException(
                                        "Incident not found with id : " + id
                                ));

        IncidentResponse response =
                incidentMapper.toResponse(incident);

        redisService.save(
                cacheKey,
                response,
                CacheConstants.INCIDENT_CACHE_TTL
        );

        return response;
    }
}
