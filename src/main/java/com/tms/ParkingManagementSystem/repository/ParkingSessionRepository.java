package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.model.ParkingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    boolean existsBySpotIdAndStatus(Long spotId, SessionStatus status);

    boolean existsByVehicleIdAndStatus(Long vehicleId, SessionStatus status);

    boolean existsByReservationId(Long reservationId);

    List<ParkingSession> findBySpotId(Long spotId);

    List<ParkingSession> findByVehicleId(Long vehicleId);

    long countBySpotParkingLotIdAndStatus(Long parkingLotId, SessionStatus status);

}
