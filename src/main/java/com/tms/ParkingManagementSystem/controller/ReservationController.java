package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.dto.ReservationCreateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationUpdateDto;
import com.tms.ParkingManagementSystem.service.ReservationService;
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
@RequestMapping("/reservation")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<List<Reservation>> getAllReservations() {
        log.info("Request: get all reservations");
        List<Reservation> reservations = reservationService.getAllReservations();
        if (reservations.isEmpty()) {
            log.warn("No reservations found");
            return ResponseEntity.noContent().build();
        }
        log.info("Found {} reservations", reservations.size());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        log.info("Request: get reservation by id = {}", id);
        Reservation reservation = reservationService.getReservationById(id);
        log.info("Reservation found id = {}", id);
        return ResponseEntity.ok(reservation);
    }

    @PostMapping
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody ReservationCreateDto dto) {
        log.info("Request: create reservation");
        log.debug("Create reservation payload: {}", dto);
        Reservation created = reservationService.createReservation(dto);
        log.info("Reservation created id = {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reservation> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationUpdateDto dto) {

        log.info("Request: update reservation id = {}", id);
        log.debug("Update reservation payload: {}", dto);

        Reservation updated = reservationService.updateReservation(id, dto);

        log.info("Reservation updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Reservation> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationStatusUpdateDto dto) {

        log.info("Request: change reservation status id = {}", id);
        log.debug("Change status payload: {}", dto);
        Reservation updated = reservationService.changeStatus(id, dto);
        log.info("Reservation status updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservationById(@PathVariable Long id) {
        log.info("Request: delete reservation id = {}", id);
        boolean deleted = reservationService.deleteReservationById(id);
        if (deleted) {
            log.info("Reservation deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to delete reservation id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("/vehicle/{vehicleId}")
    public ResponseEntity<List<Reservation>> getReservationsByVehicleId(@PathVariable Long vehicleId) {
        log.info("Request: get reservations by vehicleId = {}", vehicleId);
        List<Reservation> reservations = reservationService.getReservationsByVehicleId(vehicleId);
        if (reservations.isEmpty()) {
            log.warn("No reservations found for vehicleId = {}", vehicleId);
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} reservations for vehicleId = {}", reservations.size(), vehicleId);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/spot/{spotId}")
    public ResponseEntity<List<Reservation>> getReservationsBySpotId(@PathVariable Long spotId) {
        log.info("Request: get reservations by spotId = {}", spotId);
        List<Reservation> reservations = reservationService.getReservationsBySpotId(spotId);
        if (reservations.isEmpty()) {
            log.warn("No reservations found for spotId = {}", spotId);
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} reservations for spotId = {}", reservations.size(), spotId);
        return ResponseEntity.ok(reservations);
    }
}
