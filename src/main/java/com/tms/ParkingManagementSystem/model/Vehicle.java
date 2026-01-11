package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@ToString(exclude = "user")
public class Vehicle {

    @Id
    @SequenceGenerator(
            name = "vehicle_generator",
            sequenceName = "vehicles_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "vehicle_generator")
    private Long id;

    @NotBlank(message = "Plate number must not be blank")
    @Pattern(
            regexp = "^(?:[ABEKMHOPCTYX]\\d{3}[ABEKMHOPCTYX]{2}\\d{2,3}|\\d{4}\\s?[A-Z]{2}-\\d)$",
            message = "Plate number must match RU or BY format"
    )
    @Column(nullable = false, unique = true, length = 15)
    private String plateNumber;

    @NotNull(message = "Vehicle type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type = VehicleType.CAR;

    @NotNull(message = "User must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
