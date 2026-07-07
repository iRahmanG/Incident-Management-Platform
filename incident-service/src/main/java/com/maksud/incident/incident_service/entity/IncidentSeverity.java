package com.maksud.incident.incident_service.entity;

public enum IncidentSeverity {
    P0, // Entire system down, critical business outage
    P1, // Major functionality unavailable, Larger user impact
    P2, // Important feature broken, Workaround exists
    P3, // Minor issue, Limited impact
    P4 // Cosmetic issue, Enhancement/UI Bug
}
