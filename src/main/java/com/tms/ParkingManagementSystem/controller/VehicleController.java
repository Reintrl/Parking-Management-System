package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.VehicleCreateUpdateDto;
import com.tms.ParkingManagementSystem.service.VehicleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vehicle")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        log.info("Request: get all vehicles");
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        if (vehicles.isEmpty()) {
            log.warn("No vehicles found");
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} vehicles", vehicles.size());
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        log.info("Request: get vehicle by id = {}", id);
        Vehicle vehicle = vehicleService.getVehicleById(id);

        log.info("Vehicle found id = {}", id);
        return ResponseEntity.ok(vehicle);
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody VehicleCreateUpdateDto dto) {

        log.info("Request: create vehicle");
        log.debug("Create vehicle payload: {}", dto);
        Vehicle created = vehicleService.createVehicle(dto);

        log.info("Vehicle created id = {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleCreateUpdateDto dto) {

        log.info("Request: update vehicle id = {}", id);
        log.debug("Update vehicle payload: {}", dto);
        Vehicle updated = vehicleService.updateVehicle(id, dto);

        log.info("Vehicle updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicleById(@PathVariable Long id) {
        log.info("Request: delete vehicle id = {}", id);
        boolean deleted = vehicleService.deleteVehicleById(id);
        if (deleted) {
            log.info("Vehicle deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to delete vehicle id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Vehicle>> getAllVehicleByUserId(
            @PathVariable Long userId) {

        log.info("Request: get vehicles by userId = {}", userId);
        List<Vehicle> vehicles = vehicleService.getAllVehicleByUserId(userId);
        if (vehicles.isEmpty()) {
            log.warn("No vehicles found for userId = {}", userId);
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} vehicles for userId = {}", vehicles.size(), userId);
        return ResponseEntity.ok(vehicles);
    }
}
