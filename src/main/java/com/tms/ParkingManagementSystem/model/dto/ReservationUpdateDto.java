package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationUpdateDto {

    @NotNull(message = "Reservation end time must not be null")
    @FutureOrPresent(message = "Reservation end time cannot be in the past")
    private LocalDateTime endTime;
}
