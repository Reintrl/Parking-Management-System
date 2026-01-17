package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.dto.ParkingSessionCreateDto;
import com.tms.ParkingManagementSystem.service.ParkingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/parking-session")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSession>> getAllSessions() {
        List<ParkingSession> sessions = parkingSessionService.getAllSessions();
        if (sessions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSession> getSessionById(@PathVariable Long id) {
        Optional<ParkingSession> session = parkingSessionService.getSessionById(id);
        if (session.isPresent()) {
            return ResponseEntity.ok(session.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ParkingSession> createSession(@Valid @RequestBody ParkingSessionCreateDto dto) {
        ParkingSession created = parkingSessionService.createSession(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/from-reservation/{reservationId}")
    public ResponseEntity<ParkingSession> createSessionFromReservation(@PathVariable Long reservationId) {
        ParkingSession created = parkingSessionService.createSessionFromReservation(reservationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionById(@PathVariable Long id) {
        if (parkingSessionService.deleteSessionById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/spot/{spotId}")
    public ResponseEntity<List<ParkingSession>> getSessionsBySpotId(@PathVariable Long spotId) {
        List<ParkingSession> sessions = parkingSessionService.getSessionsBySpotId(spotId);
        if (sessions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ParkingSession>> getSessionsByVehicleId(@PathVariable Long vehicleId) {
        List<ParkingSession> sessions = parkingSessionService.getSessionsByVehicleId(vehicleId);
        if (sessions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ParkingSession> finishSession(@PathVariable Long id) {
        ParkingSession finished = parkingSessionService.finishSession(id);
        return ResponseEntity.ok(finished);
    }

}
