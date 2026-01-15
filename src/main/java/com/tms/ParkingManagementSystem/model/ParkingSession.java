package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
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
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions", indexes = {
        @Index(name = "ix_sessions_spot_status", columnList = "spot_id, status"),
        @Index(name = "ix_sessions_vehicle_status", columnList = "vehicle_id, status")
})
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class ParkingSession {

    @Id
    @SequenceGenerator(
            name = "parking_session_generator",
            sequenceName = "parking_sessions_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "parking_session_generator")
    private Long id;

    @NotNull(message = "Vehicle must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private final Vehicle vehicle;

    @NotNull(message = "Spot must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private final Spot spot;

    @NotNull(message = "Start time must not be null")
    @PastOrPresent(message = "Start time cannot be in the future")
    @Column(name = "start_time", nullable = false)
    private final LocalDateTime startTime;

    @FutureOrPresent(message = "End time cannot be in the past")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull(message = "Session status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @AssertTrue(message = "End time must be after start time")
    private boolean isEndTimeValid() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }

    @AssertTrue(message = "Finished session must have end time")
    private boolean isFinishedHasEndTime() {
        return status != SessionStatus.FINISHED || endTime != null;
    }

    @AssertTrue(message = "Active session must not have end time")
    private boolean isActiveHasNoEndTime() {
        return status != SessionStatus.ACTIVE || endTime == null;
    }
}
