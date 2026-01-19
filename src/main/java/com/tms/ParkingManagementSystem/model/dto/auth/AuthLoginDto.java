package com.tms.ParkingManagementSystem.model.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLoginDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
