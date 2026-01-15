package com.tms.ParkingManagementSystem.exception;

public class AddressAlreadyExistsException extends RuntimeException {
    public AddressAlreadyExistsException(String address) {
        super("Address already exists: " + address);
    }
}
