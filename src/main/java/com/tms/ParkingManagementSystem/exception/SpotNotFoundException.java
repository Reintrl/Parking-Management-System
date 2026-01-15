package com.tms.ParkingManagementSystem.exception;

public class SpotNotFoundException extends RuntimeException {
    public SpotNotFoundException(Long id) {
        super("Spot not found with id: " + id);
    }
}
