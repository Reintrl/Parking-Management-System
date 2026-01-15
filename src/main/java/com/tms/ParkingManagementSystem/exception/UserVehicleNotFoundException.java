package com.tms.ParkingManagementSystem.exception;

public class UserVehicleNotFoundException extends RuntimeException {
    public UserVehicleNotFoundException(Long userId) {
        super("User vehicle not found with user id: " + userId);
    }
}
