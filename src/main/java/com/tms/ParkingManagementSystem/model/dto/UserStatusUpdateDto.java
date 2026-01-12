package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.UserStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserStatusUpdateDto {
    @NotNull(message = "Status must not be null")
    private UserStatus status;
}
