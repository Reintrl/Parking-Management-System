package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SpotType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@AllArgsConstructor
@Data
public class SpotCreateDto {

    @NotNull(message = "Spot number must not be null")
    @Min(value = 1, message = "Spot number must be greater than 0")
    private Integer number;

    @NotNull(message = "Spot type must not be null")
    private SpotType type;

    @NotNull(message = "Parking lot id must be specified")
    private Long parkingLotId;

    @Range(min = -3, max = 5, message = "Level must be between -3 and 5")
    private Integer level;
}
