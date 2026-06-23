package com.maksud.incident.incident_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "incidents")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Incident {
    @Id
    private UUID id;
    @Column(nullable = false)
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private IncidentSource source;
    @Enumerated(EnumType.STRING)
    private IncidentSeverity incidentSeverity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IncidentStatus status;
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private UUID createdBy;
    private UUID assignedTo;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private String resolutionSummary;
}
