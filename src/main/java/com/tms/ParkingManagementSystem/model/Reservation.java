package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;

import java.time.LocalDateTime;

public class Reservation {
    private Integer id;
    private User user;
    private Vehicle vehicle;
    private Spot spot;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private ReservationStatus status = ReservationStatus.ACTIVE;
}
