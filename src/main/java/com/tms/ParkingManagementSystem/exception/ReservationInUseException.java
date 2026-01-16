package com.tms.ParkingManagementSystem.exception;

public class ReservationInUseException extends RuntimeException {

    public ReservationInUseException(Long reservationId) {
        super("Reservation with id = " + reservationId + " is in use and cannot be deleted");
    }
}
