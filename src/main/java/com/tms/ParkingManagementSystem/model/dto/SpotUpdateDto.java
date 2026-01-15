package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SpotType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class SpotUpdateDto {

    @NotNull(message = "Spot type must not be null")
    private SpotType type;
}
