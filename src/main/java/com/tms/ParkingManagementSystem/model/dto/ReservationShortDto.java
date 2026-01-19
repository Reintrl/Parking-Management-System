package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ReservationShortDto {

    private Long id;
    private ReservationStatus status;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
