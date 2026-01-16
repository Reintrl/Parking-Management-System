package com.tms.ParkingManagementSystem.exception;

public class ParkingSessionConflictException extends RuntimeException {

    public ParkingSessionConflictException(String message) {
        super(message);
    }
}
