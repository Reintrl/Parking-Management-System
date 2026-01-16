package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ParkingSessionStatusUpdateDto {

    @NotNull(message = "Session status must not be null")
    private SessionStatus status;

    private LocalDateTime endTime;
}
