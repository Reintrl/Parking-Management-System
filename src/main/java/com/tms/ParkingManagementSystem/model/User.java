package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.UserStatus;

import java.time.LocalDateTime;

public class User {
    private Integer id;
    private String email;
    private UserStatus status;
    private LocalDateTime created;
    private LocalDateTime changed;
    private boolean disabledPermit;
}
