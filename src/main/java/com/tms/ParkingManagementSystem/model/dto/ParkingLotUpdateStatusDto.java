package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.ParkingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ParkingLotUpdateStatusDto {
    @NotNull(message = "Status must not be null")
    ParkingStatus status;
}
