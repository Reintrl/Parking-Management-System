package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.ParkingStatus;
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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "parking_lots")
@Data
public class ParkingLot {

    @Id
    @SequenceGenerator(
            name = "parking_lot_generator",
            sequenceName = "parking_lots_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "parking_lot_generator")
    private Long id;

    @NotBlank(message = "Parking lot name must not be blank")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Parking lot address must not be blank")
    @Size(min = 2, max = 50, message = "Address must be between 2 and 50 characters")
    @Column(nullable = false, unique = true)
    private String address;

    @NotNull(message = "Parking lot status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingStatus status = ParkingStatus.ACTIVE;

    @NotNull(message = "Tariff must be specified")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "tariff_id", nullable = false)
    private Tariff tariff;
}