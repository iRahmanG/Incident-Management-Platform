package com.maksud.incident.incident_service.controller;

import com.maksud.incident.incident_service.dto.AssignIncidentRequest;
import com.maksud.incident.incident_service.dto.CreateIncidentRequest;
import com.maksud.incident.incident_service.dto.IncidentResponse;
import com.maksud.incident.incident_service.security.AuthenticatedUser;
import com.maksud.incident.incident_service.security.UserContext;
import com.maksud.incident.incident_service.service.IncidentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<IncidentResponse> acknowledge(@PathVariable UUID id, HttpServletRequest request){

    }

//    @GetMapping
//    public ResponseEntity<Page<IncidentResponse>> getAllIncidents(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size
//    ){}
}
