package com.maksud.incident.incident_service.controller;

import com.maksud.incident.incident_service.dto.AssignIncidentRequest;
import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.dto.ResolveIncidentRequest;
import com.maksud.incident.incident_service.security.AuthenticatedUser;
import com.maksud.incident.incident_service.security.UserContext;
import com.maksud.incident.incident_service.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

}
