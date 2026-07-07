package com.maksud.incident.incident_service.controller;

import com.maksud.incident.incident_service.dto.*;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import com.maksud.incident.incident_service.security.AuthenticatedUser;
import com.maksud.incident.incident_service.security.UserContext;
import com.maksud.incident.incident_service.service.IncidentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentService incidentService;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<IncidentResponse> createIncident(@Valid @RequestBody CreateIncidentRequest request){
        AuthenticatedUser user = userContext.getCurrentUser();
        IncidentResponse response = incidentService.createIncident(request, user.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<IncidentSummaryResponse>> getAllIncidents(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            @RequestParam(required = false)IncidentStatus status,
            @RequestParam(required = false)IncidentSeverity severity
            ) {
        Page<IncidentSummaryResponse> incidents = incidentService.getAllIncidents(page, size, status, severity);
        return ResponseEntity.ok(incidents);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IncidentResponse> getIncidentById(@PathVariable UUID id) {
        IncidentResponse response =
                incidentService.getIncidentById(id);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<IncidentResponse> assignIncident(@PathVariable UUID id, @RequestBody @Valid AssignIncidentRequest request){
        IncidentResponse response = incidentService.assignIncident(id,request.engineerId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/acknowledge")
    public ResponseEntity<IncidentResponse> acknowledge(@PathVariable UUID id) throws AccessDeniedException {
        AuthenticatedUser user = userContext.getCurrentUser();
        IncidentResponse response  = incidentService.acknowledgeIncident(id, user.userId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<IncidentResponse> resolveIncident(@PathVariable UUID id, @RequestBody ResolveIncidentRequest request) throws AccessDeniedException {
        AuthenticatedUser user = userContext.getCurrentUser();
        IncidentResponse response = incidentService.resolveIncident(id, user.userId(), request.resolutionSummary());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<IncidentResponse> closeIncident(@PathVariable UUID id, @RequestBody CloseIncidentRequest request){
        IncidentResponse response = incidentService.closeIncident(id, request.closureSummary());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reopen")
    public ResponseEntity<IncidentResponse> reopenIncident(@PathVariable UUID id, @RequestBody ReopenIncidentRequest request){
        IncidentResponse response = incidentService.reopenIncident(id, request.reason());
        return ResponseEntity.ok(response);
    }


}
