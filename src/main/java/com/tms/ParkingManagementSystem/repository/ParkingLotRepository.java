package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.model.ParkingLot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingLotRepository extends JpaRepository<ParkingLot, Long> {

    boolean existsParkingLotByAddress(String address);

    boolean existsByTariffId(Long id);

    int countByTariffId(Long tariffId);
}
