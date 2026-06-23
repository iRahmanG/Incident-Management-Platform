package com.maksud.incident.incident_service.repository;

import com.maksud.incident.incident_service.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IncidentRepository extends JpaRepository<Incident, UUID> {
}
