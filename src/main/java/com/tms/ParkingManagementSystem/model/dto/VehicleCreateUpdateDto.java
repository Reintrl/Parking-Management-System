package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VehicleCreateUpdateDto {

    @NotBlank(message = "Plate number must not be blank")
    @Pattern(
            regexp = "^(?:\\d{4}\\s[ABEKMHOPCTXI]{2}-[1-8]|" +
                    "E\\d{3}\\s[ABEKMHOPCTXI]{2}-[1-8]|" +
                    "[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\d{2,3})$",
            message = "Plate number must match RU or BY format"
    )
    private String plateNumber;

    @NotNull(message = "Vehicle type must not be null")
    private VehicleType type;

    @NotNull(message = "User id must be specified")
    private Long userId;
}
