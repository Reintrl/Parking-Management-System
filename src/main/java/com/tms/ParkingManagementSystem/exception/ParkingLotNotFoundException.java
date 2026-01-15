package com.tms.ParkingManagementSystem.exception;

public class ParkingLotNotFoundException extends RuntimeException {
    public ParkingLotNotFoundException(Long id) {
        super("ParkingLot not found with id:" + id);
    }
}
