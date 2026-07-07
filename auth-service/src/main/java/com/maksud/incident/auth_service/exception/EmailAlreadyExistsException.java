package com.maksud.incident.auth_service.exception;

public class EmailAlreadyExistsException  extends RuntimeException{
    public EmailAlreadyExistsException(String email){
        super("Email Already exists: "+ email);
    }
}
