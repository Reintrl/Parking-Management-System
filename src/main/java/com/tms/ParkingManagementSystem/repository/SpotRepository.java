package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.model.Spot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpotRepository extends JpaRepository<Spot,Long> {
    boolean existsByParkingLotIdAndNumber(Long parkingLotId, Integer number);
}
