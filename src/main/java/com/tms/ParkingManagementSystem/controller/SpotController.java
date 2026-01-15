package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.dto.SpotCreateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotUpdateDto;
import com.tms.ParkingManagementSystem.service.SpotService;
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
import java.util.Optional;

@RestController
@RequestMapping("/spot")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public ResponseEntity<List<Spot>> getAllSpots() {
        List<Spot> spots = spotService.getAllSpots();
        if (spots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spot> getSpotById(@PathVariable Long id) {
        Optional<Spot> spot = spotService.getSpotById(id);
        if (spot.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spot.get());
    }

    @PostMapping
    public ResponseEntity<Spot> createSpot(@Valid @RequestBody SpotCreateDto dto) {
        Spot created = spotService.createSpot(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Spot> updateSpot(
            @PathVariable Long id,
            @Valid @RequestBody SpotUpdateDto dto) {

        Spot updated = spotService.updateSpot(id, dto);
        return ResponseEntity.ok(updated);
    }


    @PatchMapping("/{id}/status")
    public ResponseEntity<Spot> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody SpotStatusUpdateDto dto) {

        Spot updated = spotService.changeStatus(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpotById(@PathVariable Long id) {
        if (spotService.deleteSpotById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/parkingLot/{parkingLotId}")
    public ResponseEntity<List<Spot>> getSpotsByParkingLotId(
            @PathVariable Long parkingLotId) {

        List<Spot> spots = spotService.getSpotsByParkingLotId(parkingLotId);

        if (spots.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(spots);
    }

}
