package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {

    @Id
    @SequenceGenerator(
            name = "reservation_generator",
            sequenceName = "reservations_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "reservation_generator")
    private Long id;

    @NotNull(message = "Vehicle must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "Spot must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @NotNull(message = "Reservation start time must not be null")
    @PastOrPresent(message = "Reservation start time cannot be in the future")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @NotNull(message = "Reservation end time must not be null")
    @PastOrPresent(message = "Reservation end time cannot be in the future")
    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @NotNull(message = "Reservation status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status = ReservationStatus.ACTIVE;

    @AssertTrue(message = "Reservation end time must be after start time")
    private boolean isReservationTimeValid() {
        return startTime == null || endTime == null || endTime.isAfter(startTime);
    }

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime changed;
}
