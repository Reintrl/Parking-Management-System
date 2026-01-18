package com.tms.ParkingManagementSystem.exception;

public class ParkingLotInUseException extends RuntimeException {

    public ParkingLotInUseException(Long parkingLotId, String reason) {
        super("Parking lot with id = " + parkingLotId + " is in use and cannot be deleted"
                + (reason == null || reason.isBlank() ? "" : ": " + reason));
    }
}
