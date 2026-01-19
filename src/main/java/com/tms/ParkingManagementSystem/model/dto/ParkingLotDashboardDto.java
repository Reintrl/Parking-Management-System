package com.tms.ParkingManagementSystem.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParkingLotDashboardDto {
    private long totalSpots;
    private long availableSpots;
    private long occupiedSpots;
    private long reservedSpots;
    private long outOfServiceSpots;
    private long activeReservations;
    private long activeSessions;
}
