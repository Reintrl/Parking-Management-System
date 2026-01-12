package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.TariffStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TariffStatusUpdateDto {
    @NotNull(message = "Status must not be null")
    private TariffStatus status;
}
