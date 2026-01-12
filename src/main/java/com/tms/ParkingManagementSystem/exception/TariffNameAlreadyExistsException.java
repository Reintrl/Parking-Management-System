package com.tms.ParkingManagementSystem.exception;

public class TariffNameAlreadyExistsException extends RuntimeException {
    public TariffNameAlreadyExistsException(String tariffName) {
        super("Tariff name already exists: " + tariffName);
    }
}
