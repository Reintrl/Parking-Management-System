package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotUpdateStatusDto;
import com.tms.ParkingManagementSystem.service.ParkingLotService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/parkingLot")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllParkingLots() {
        List<ParkingLot> parkingLots = parkingLotService.getAllParkingLots();
        if (parkingLots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(parkingLots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingLot> getParkingLotById(@PathVariable Long id) {
        ParkingLot parkingLot = parkingLotService.getParkingLotById(id);
        return ResponseEntity.ok(parkingLot);
    }

    @PostMapping
    public ResponseEntity<ParkingLot> createParkingLot(@Valid @RequestBody ParkingLotCreateUpdateDto parkingLotCreateUpdateDto) {

        ParkingLot saved = parkingLotService.createParkingLot(parkingLotCreateUpdateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ParkingLot> updateParkingLot(
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotCreateUpdateDto dto) {
        ParkingLot updated = parkingLotService.updateParkingLot(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteParkingLotById(@PathVariable Long id) {
        if (parkingLotService.deleteParkingLotById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ParkingLot> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ParkingLotUpdateStatusDto dto) {
        ParkingLot updated = parkingLotService.changeStatus(id, dto);
        return ResponseEntity.ok(updated);
    }
}
