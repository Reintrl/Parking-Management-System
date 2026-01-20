package com.tms.ParkingManagementSystem.exception;

public class SecurityNotFoundException extends RuntimeException {

    public SecurityNotFoundException(Long id) {
        super("Security with id = " + id + " was not found");
    }
}
