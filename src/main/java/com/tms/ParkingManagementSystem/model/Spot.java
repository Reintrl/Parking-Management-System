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
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "spots",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_spots_parking_lot_number", columnNames = {"parking_lot_id", "number"})
        },
        indexes = {
                @Index(name = "ix_spots_parking_lot_id", columnList = "parking_lot_id")
        })
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Spot {

    @Id
    @SequenceGenerator(
            name = "spot_generator",
            sequenceName = "spots_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "spot_generator")
    private Long id;

    @Min(value = 1, message = "Spot number must be greater than 0")
    @Column(nullable = false)
    private final Integer number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotType type = SpotType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SpotStatus status = SpotStatus.AVAILABLE;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parking_lot_id", nullable = false)
    private final ParkingLot parkingLot;

    @Column
    private final Integer level;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime changed;
}
