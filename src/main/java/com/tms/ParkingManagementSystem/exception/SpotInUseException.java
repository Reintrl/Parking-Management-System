package com.tms.ParkingManagementSystem.exception;

public class SpotInUseException extends RuntimeException {

    public SpotInUseException(Long spotId, String reason) {
        super("Spot with id = " + spotId + " is in use and cannot be deleted"
                + (reason == null || reason.isBlank() ? "" : ": " + reason));
    }
}