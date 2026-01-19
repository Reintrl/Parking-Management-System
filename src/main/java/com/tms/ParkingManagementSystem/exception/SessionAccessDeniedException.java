package com.tms.ParkingManagementSystem.exception;

public class SessionAccessDeniedException extends RuntimeException {

    public SessionAccessDeniedException(Long sessionId) {
        super("Access denied to parking session with id = " + sessionId);
    }
}
