package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserCreateUpdateDto {
    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Second name must not be blank")
    @Size(min = 2, max = 50, message = "Second name must be between 2 and 50 characters")
    private String secondName;

    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be valid")
    private String email;

    @NotNull(message = "Disabled permit flag must not be null")
    private Boolean disabledPermit;
}
