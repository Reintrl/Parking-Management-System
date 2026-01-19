package com.tms.ParkingManagementSystem.exception;

public class ReservationAccessDeniedException extends RuntimeException {
    public ReservationAccessDeniedException(Long reservationId) {
        super("Access denied to reservation with id = " + reservationId);
    }
}
