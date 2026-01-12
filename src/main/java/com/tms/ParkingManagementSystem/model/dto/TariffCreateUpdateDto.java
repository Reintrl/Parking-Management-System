package com.tms.ParkingManagementSystem.model.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class TariffCreateUpdateDto {

    @NotBlank(message = "Tariff name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Hour price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hour price must be greater than 0")
    private BigDecimal hourPrice;

    @NotNull(message = "Billing step minutes must not be null")
    @Min(value = 1, message = "Billing step minutes must be greater than 0")
    private Integer billingStepMinutes;

    @NotNull(message = "Free minutes must not be null")
    @Min(value = 0, message = "Free minutes must be zero or positive")
    private Integer freeMinutes;
}
