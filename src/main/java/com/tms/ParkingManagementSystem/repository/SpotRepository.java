package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;
import com.tms.ParkingManagementSystem.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SpotRepository extends JpaRepository<Spot,Long> {

    boolean existsByParkingLotIdAndNumber(Long parkingLotId, Integer number);

    List<Spot> findByParkingLotId(Long parkingLotId);

    void deleteAllByParkingLotId(Long parkingLotId);

    boolean existsByParkingLotIdAndLevelAndNumber(Long parkingLotId, Integer level, Integer number);

    @Query("""
        select s
        from Spot s
        where s.parkingLot.id = :parkingLotId
          and s.status = com.tms.ParkingManagementSystem.enums.SpotStatus.AVAILABLE
          and (:type is null or s.type = :type)
          and not exists (
              select 1
              from ParkingSession ps
              where ps.spot = s
                and ps.status = :activeSessionStatus
          )
          and not exists (
              select 1
              from Reservation r
              where r.spot = s
                and r.status = com.tms.ParkingManagementSystem.enums.ReservationStatus.ACTIVE
                and r.startTime < :to
                and r.endTime > :from
          )
        order by s.number asc
        """)
    List<Spot> findAvailableSpots(
            @Param("parkingLotId") Long parkingLotId,
            @Param("type") SpotType type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("activeSessionStatus") SessionStatus activeSessionStatus
    );

    Long countByParkingLotId(Long parkingLotId);

    Long countByParkingLotIdAndStatus(Long parkingLotId, SpotStatus status);
}
