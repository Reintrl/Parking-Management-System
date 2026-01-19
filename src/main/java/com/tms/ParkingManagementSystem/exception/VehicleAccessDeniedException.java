package com.tms.ParkingManagementSystem.exception;

public class VehicleAccessDeniedException extends RuntimeException {
    public VehicleAccessDeniedException(Long vehicleId) {
        super("Access denied to vehicle with id = " + vehicleId);
    }
}
