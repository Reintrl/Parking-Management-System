package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.dto.SpotCreateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotUpdateDto;
import com.tms.ParkingManagementSystem.service.SpotService;
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
@RequestMapping("/spot")
public class SpotController {

    private final SpotService spotService;

    public SpotController(SpotService spotService) {
        this.spotService = spotService;
    }

    @GetMapping
    public ResponseEntity<List<Spot>> getAllSpots() {
        log.info("Request: get all spots");
        List<Spot> spots = spotService.getAllSpots();

        if (spots.isEmpty()) {
            log.warn("No spots found");
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} spots", spots.size());
        return ResponseEntity.ok(spots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Spot> getSpotById(@PathVariable Long id) {
        log.info("Request: get spot by id = {}", id);
        Spot spot = spotService.getSpotById(id);
        log.info("Spot found id = {}", id);
        return ResponseEntity.ok(spot);
    }

    @PostMapping
    public ResponseEntity<Spot> createSpot(@Valid @RequestBody SpotCreateDto dto) {
        log.info("Request: create spot");
        log.debug("Create spot payload: {}", dto);
        Spot created = spotService.createSpot(dto);
        log.info("Spot created id = {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Spot> updateSpot(
            @PathVariable Long id,
            @Valid @RequestBody SpotUpdateDto dto) {

        log.info("Request: update spot id = {}", id);
        log.debug("Update spot payload: {}", dto);
        Spot updated = spotService.updateSpot(id, dto);
        log.info("Spot updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Spot> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody SpotStatusUpdateDto dto) {

        log.info("Request: change spot status id = {}", id);
        log.debug("Change status payload: {}", dto);
        Spot updated = spotService.changeStatus(id, dto);
        log.info("Spot status updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpotById(@PathVariable Long id) {
        log.info("Request: delete spot id = {}", id);

        boolean deleted = spotService.deleteSpotById(id);
        if (deleted) {
            log.info("Spot deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to delete spot id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/parkingLot/{parkingLotId}")
    public ResponseEntity<List<Spot>> getSpotsByParkingLotId(
            @PathVariable Long parkingLotId) {

        log.info("Request: get spots by parkingLotId = {}", parkingLotId);
        List<Spot> spots = spotService.getSpotsByParkingLotId(parkingLotId);

        if (spots.isEmpty()) {
            log.warn("No spots found for parkingLotId = {}", parkingLotId);
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} spots for parkingLotId = {}", spots.size(), parkingLotId);
        return ResponseEntity.ok(spots);
    }
}
