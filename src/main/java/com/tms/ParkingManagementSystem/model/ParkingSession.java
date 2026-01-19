package com.tms.ParkingManagementSystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_sessions", indexes = {
        @Index(name = "ix_sessions_spot_status", columnList = "spot_id, status"),
        @Index(name = "ix_sessions_vehicle_status", columnList = "vehicle_id, status")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private final Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "spot_id", nullable = false)
    private final Spot spot;

    @Column(name = "start_time", nullable = false)
    private final LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;
}
