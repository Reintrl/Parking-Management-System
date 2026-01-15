package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SpotStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SpotStatusUpdateDto {

    @NotNull(message = "Spot status must not be null")
    private SpotStatus status;
}
