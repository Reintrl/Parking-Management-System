package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserMeDto {
    private String email;
    private String firstName;
    private String secondName;
    private boolean disabledPermit;
    private UserStatus status;
}
