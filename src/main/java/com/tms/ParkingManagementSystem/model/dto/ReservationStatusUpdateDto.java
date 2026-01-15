package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationStatusUpdateDto {

    @NotNull(message = "Reservation status must not be null")
    private ReservationStatus status;
}
