package com.tms.ParkingManagementSystem.exception;

public class ReservationNotFoundException extends RuntimeException {

    public ReservationNotFoundException(Long id) {
        super("Reservation with id=" + id + " was not found");
    }
}
