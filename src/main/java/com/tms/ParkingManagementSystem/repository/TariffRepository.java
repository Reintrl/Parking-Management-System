package com.tms.ParkingManagementSystem.repository;

import com.tms.ParkingManagementSystem.model.Tariff;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TariffRepository extends JpaRepository<Tariff, Long> {
    boolean existsByName(String name);
}
