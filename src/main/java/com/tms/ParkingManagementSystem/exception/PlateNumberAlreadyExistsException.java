package com.tms.ParkingManagementSystem.exception;

public class PlateNumberAlreadyExistsException extends RuntimeException {
    public PlateNumberAlreadyExistsException(String plateNumber) {
        super("Plate number already exists: " + plateNumber);
    }
}
