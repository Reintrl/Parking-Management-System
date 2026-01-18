package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.dto.ParkingSessionCreateDto;
import com.tms.ParkingManagementSystem.service.ParkingSessionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/parkingSession")
public class ParkingSessionController {

    private final ParkingSessionService parkingSessionService;

    public ParkingSessionController(ParkingSessionService parkingSessionService) {
        this.parkingSessionService = parkingSessionService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingSession>> getAllSessions() {
        log.info("Request: get all parking sessions");
        List<ParkingSession> sessions = parkingSessionService.getAllSessions();
        if (sessions.isEmpty()) {
            log.warn("No parking sessions found");
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} parking sessions", sessions.size());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ParkingSession> getSessionById(@PathVariable Long id) {
        log.info("Request: get parking session by id = {}", id);
        ParkingSession session = parkingSessionService.getSessionById(id);
        log.info("Parking session found id = {}", id);
        return ResponseEntity.ok(session);
    }

    @PostMapping
    public ResponseEntity<ParkingSession> createSession(@Valid @RequestBody ParkingSessionCreateDto dto) {
        log.info("Request: create parking session");
        log.debug("Create session payload: {}", dto);
        ParkingSession created = parkingSessionService.createSession(dto);
        log.info("Parking session created id = {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/from-reservation/{reservationId}")
    public ResponseEntity<ParkingSession> createSessionFromReservation(@PathVariable Long reservationId) {
        log.info("Request: create parking session from reservation id = {}", reservationId);
        ParkingSession created = parkingSessionService.createSessionFromReservation(reservationId);
        log.info("Parking session created from reservation id = {}, sessionId = {}", reservationId, created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSessionById(@PathVariable Long id) {
        log.info("Request: delete parking session id = {}", id);
        boolean deleted = parkingSessionService.deleteSessionById(id);
        if (deleted) {
            log.info("Parking session deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }
        log.error("Failed to delete parking session id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/spot/{spotId}")
    public ResponseEntity<List<ParkingSession>> getSessionsBySpotId(@PathVariable Long spotId) {
        log.info("Request: get parking sessions by spotId = {}", spotId);
        List<ParkingSession> sessions = parkingSessionService.getSessionsBySpotId(spotId);
        if (sessions.isEmpty()) {
            log.warn("No parking sessions found for spotId = {}", spotId);
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} parking sessions for spotId = {}",
                sessions.size(), spotId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<ParkingSession>> getSessionsByVehicleId(@PathVariable Long vehicleId) {
        log.info("Request: get parking sessions by vehicleId = {}", vehicleId);
        List<ParkingSession> sessions = parkingSessionService.getSessionsByVehicleId(vehicleId);
        if (sessions.isEmpty()) {
            log.warn("No parking sessions found for vehicleId = {}", vehicleId);
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} parking sessions for vehicleId = {}", sessions.size(), vehicleId);
        return ResponseEntity.ok(sessions);
    }

    @PostMapping("/{id}/finish")
    public ResponseEntity<ParkingSession> finishSession(@PathVariable Long id) {
        log.info("Request: finish parking session id = {}", id);
        ParkingSession finished = parkingSessionService.finishSession(id);
        log.info("Parking session finished id = {}", id);
        return ResponseEntity.ok(finished);
    }
}
