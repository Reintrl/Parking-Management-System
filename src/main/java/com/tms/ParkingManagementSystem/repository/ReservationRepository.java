package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByVehicleId(Long vehicleId);
    List<Reservation> findBySpotId(Long spotId);
    boolean existsBySpotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            Long spotId,
            ReservationStatus status,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
    List<Reservation> findByStatusAndEndTimeBefore(ReservationStatus status, LocalDateTime time);
    boolean existsBySpotIdAndStatusAndEndTimeAfter(Long spotId, ReservationStatus status, LocalDateTime time);
    boolean existsByVehicleIdAndStatusAndEndTimeAfter(
            Long vehicleId,
            ReservationStatus status,
            LocalDateTime time
    );
    boolean existsBySpotIdAndStatusAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
            Long spotId,
            ReservationStatus status,
            Long id,
            LocalDateTime endTime,
            LocalDateTime startTime
    );
}
