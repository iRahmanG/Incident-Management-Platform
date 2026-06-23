package com.maksud.incident.incident_service.repository;

import com.maksud.incident.incident_service.entity.Incident;
import com.maksud.incident.incident_service.entity.IncidentSeverity;
import com.maksud.incident.incident_service.entity.IncidentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {

    Page<Incident> findByStatus(IncidentStatus status, Pageable pageable);

    Page<Incident> findByIncidentSeverity(IncidentSeverity severity, Pageable pageable);

    Page<Incident> findByStatusAndIncidentSeverity(
            IncidentStatus status,
            IncidentSeverity severity,
            Pageable pageable
    );



}
