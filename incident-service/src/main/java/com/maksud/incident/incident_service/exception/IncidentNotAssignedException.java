package com.maksud.incident.incident_service.exception;

public class IncidentNotAssignedException extends RuntimeException{
    public IncidentNotAssignedException(String message){
        super(message);
    }
}
