package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ParkingSessionResponseDto {

    private Long id;
    private SessionStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private VehicleShortDto vehicle;
    private SpotShortDto spot;

    private ReservationShortDto reservation;

    private BigDecimal totalCost;
}
