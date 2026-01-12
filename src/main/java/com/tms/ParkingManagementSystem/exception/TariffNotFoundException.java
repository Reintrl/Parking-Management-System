package com.tms.ParkingManagementSystem.exception;

public class TariffNotFoundException extends RuntimeException {
    public TariffNotFoundException(Long id) {
        super("Tariff not found with id: " + id);
    }
}
