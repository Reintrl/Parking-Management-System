package com.tms.ParkingManagementSystem.exception;

public class VehicleInUseException extends RuntimeException {

    public VehicleInUseException(Long vehicleId, String reason) {
        super("Vehicle with id = " + vehicleId + " is in use and cannot be deleted"
                + (reason == null || reason.isBlank() ? "" : ": " + reason));
    }
}
