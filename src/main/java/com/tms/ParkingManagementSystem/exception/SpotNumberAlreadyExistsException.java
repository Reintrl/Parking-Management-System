package com.tms.ParkingManagementSystem.exception;

public class SpotNumberAlreadyExistsException extends RuntimeException {
    public SpotNumberAlreadyExistsException(Long parkingLotId, Integer number) {
        super("Spot with number '" + number + "' already exists in parking lot with id: " + parkingLotId);
    }
}
