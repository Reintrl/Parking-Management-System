package com.tms.ParkingManagementSystem.exception;

public class ParkingSessionNotFoundException extends RuntimeException {

    public ParkingSessionNotFoundException(Long id) {
        super("Parking session with id = " + id + " was not found");
    }
}
