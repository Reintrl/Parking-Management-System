package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
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
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions")
@Getter
@Setter
@ToString(exclude = {"vehicle", "spot"})
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
    private Vehicle vehicle;

    @NotNull(message = "Spot must be specified")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private Spot spot;

    @NotNull(message = "Start time must not be null")
    @PastOrPresent(message = "Start time cannot be in the future")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @PastOrPresent(message = "End time cannot be in the future")
    @Column(name = "end_time")
    private LocalDateTime endTime;

    @NotNull(message = "Session status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @AssertTrue(message = "End time must be after start time")
    private boolean isEndTimeValid() {
        return endTime == null || startTime == null || endTime.isAfter(startTime);
    }
}
