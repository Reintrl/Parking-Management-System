package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.VehicleType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "vehicles")
@Data
public class Vehicle {

    @Id
    @SequenceGenerator(
            name = "vehicle_generator",
            sequenceName = "vehicles_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "vehicle_generator")
    private Long id;

    @Column(nullable = false, unique = true, length = 15)
    private String plateNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type = VehicleType.CAR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
