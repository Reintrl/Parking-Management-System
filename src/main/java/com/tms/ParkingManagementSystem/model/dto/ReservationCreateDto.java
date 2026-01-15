package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateDto {

    @NotNull(message = "Vehicle id must be specified")
    private Long vehicleId;

    @NotNull(message = "Spot id must be specified")
    private Long spotId;

    @NotNull(message = "Reservation start time must not be null")
    @FutureOrPresent(message = "Reservation start time cannot be in the past")
    private LocalDateTime startTime;

    @NotNull(message = "Reservation end time must not be null")
    @FutureOrPresent(message = "Reservation end time cannot be in the past")
    private LocalDateTime endTime;

    @AssertTrue(message = "Reservation end time must be after start time")
    private boolean isReservationTimeValid() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }
}
