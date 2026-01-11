package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;
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
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "spots")
@Getter
@Setter
@ToString(exclude = "parkingLot")
public class Spot {

    @Id
    @SequenceGenerator(
            name = "spot_generator",
            sequenceName = "spots_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "spot_generator")
    private Long id;

    @NotNull(message = "Spot number must not be null")
    @Min(value = 1, message = "Spot number must be greater than 0")
    @Column(nullable = false)
    private Integer number;

    @NotNull(message = "Spot type must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotType type = SpotType.STANDARD;

    @NotNull(message = "Spot status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status = SpotStatus.AVAILABLE;

    @NotNull(message = "Parking lot must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private ParkingLot parkingLot;

    @Min(value = 0, message = "Level must be zero or positive")
    @Column
    private Integer level;
}
