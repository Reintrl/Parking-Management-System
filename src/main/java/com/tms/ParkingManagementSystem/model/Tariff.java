package com.tms.ParkingManagementSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
@Data
public class Tariff {

    @Id
    @SequenceGenerator(
            name = "tariff_generator",
            sequenceName = "tariffs_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "tariff_generator")
    private Long id;

    @NotBlank(message = "Tariff name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull(message = "Hour price must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hour price must be greater than 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourPrice;

    @NotNull(message = "Billing step minutes must not be null")
    @Min(value = 1, message = "Billing step minutes must be greater than 0")
    @Column(nullable = false)
    private Integer billingStepMinutes;

    @NotNull(message = "Free minutes must not be null")
    @Min(value = 0, message = "Free minutes must be zero or positive")
    @Column(nullable = false)
    private Integer freeMinutes = 0;

    @NotNull(message = "Active flag must not be null")
    @Column(nullable = false)
    private Boolean active = true;
}
