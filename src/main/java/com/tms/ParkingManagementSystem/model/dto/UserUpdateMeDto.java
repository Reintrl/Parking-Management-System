package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserUpdateMeDto {

    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @NotBlank(message = "Second name must not be blank")
    @Size(min = 2, max = 50, message = "Second name must be between 2 and 50 characters")
    private String secondName;
}
