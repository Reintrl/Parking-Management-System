package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
import java.time.LocalDateTime;

public class ParkingSession {
    private Integer id;
    private Integer parkingLotId;
    private Integer spotId;
    private Integer vehicleId;
    private Integer userId;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private SessionStatus status;
}
