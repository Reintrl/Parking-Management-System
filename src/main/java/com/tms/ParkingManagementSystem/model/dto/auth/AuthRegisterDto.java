package com.tms.ParkingManagementSystem.model.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AuthRegisterDto {

    @NotBlank
    private String firstName;

    @NotBlank
    private String secondName;

    @Email
    @NotBlank
    private String email;

    @NotNull
    private Boolean disabledPermit;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;
}
