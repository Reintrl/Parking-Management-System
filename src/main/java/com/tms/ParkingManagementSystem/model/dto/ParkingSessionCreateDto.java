package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingSessionCreateDto {

    @NotNull(message = "Vehicle id must be specified")
    private Long vehicleId;

    @NotNull(message = "Spot id must be specified")
    private Long spotId;

    private Long reservationId;
}
