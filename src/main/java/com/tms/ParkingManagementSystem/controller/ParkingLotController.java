package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotUpdateStatusDto;
import com.tms.ParkingManagementSystem.service.ParkingLotService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/parkingLot")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
        log.info("Request: get all parking lots");
        List<ParkingLot> parkingLots = parkingLotService.getAllParkingLots();
        if (parkingLots.isEmpty()) {
            log.warn("No parking lots found");
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} parking lots", parkingLots.size());
        return ResponseEntity.ok(parkingLots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable Long id) {
        log.info("Request: get parking lot by id = {}", id);
        ParkingLot parkingLot = parkingLotService.getParkingLotById(id);
        log.info("Parking lot found with id = {}", id);
        return ResponseEntity.ok(parkingLot);
    }

    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(@Valid @RequestBody ParkingLotCreateUpdateDto parkingLotCreateUpdateDto) {
        log.info("Request: create parking lot");
        log.debug("Create parking lot payload: {}", parkingLotCreateUpdateDto);
        ParkingLot saved = parkingLotService.createParkingLot(parkingLotCreateUpdateDto);
        log.info("Parking lot created with id = {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> updateParkingLot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotCreateUpdateDto dto) {

        log.info("Request: update parking lot id = {}", id);
        log.debug("Update parking lot payload: {}", dto);
        ParkingLot updated = parkingLotService.updateParkingLot(id, dto);
        log.info("Parking lot updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLotById(@PathVariable Long id) {
        log.info("Request: delete parking lot id = {}", id);
        if (parkingLotService.deleteParkingLotById(id)) {
            log.info("Parking lot deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }
        log.error("Failed to delete parking lot id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ParkingLot> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotUpdateStatusDto dto) {
        log.info("Request: change parking lot status id = {}", id);
        log.debug("Change status payload: {}", dto);
        ParkingLot updated = parkingLotService.changeStatus(id, dto);
        log.info("Parking lot status updated id = {}", id);
        return ResponseEntity.ok(updated);
    }
}
