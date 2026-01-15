package com.tms.ParkingManagementSystem.exception;

public class TariffInUseException extends RuntimeException {
    public TariffInUseException(Long tariffId) {
        super("Tariff with id: " + tariffId + " cannot be deleted because it is assigned to one or more parking lots");
    }
}
