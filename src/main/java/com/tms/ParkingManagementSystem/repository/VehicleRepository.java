package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByPlateNumber(String plateNumber);

    List<Vehicle> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
