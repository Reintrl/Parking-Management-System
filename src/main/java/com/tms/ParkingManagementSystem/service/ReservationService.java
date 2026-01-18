package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.exception.ReservationConflictException;
import com.tms.ParkingManagementSystem.exception.ReservationInUseException;
import com.tms.ParkingManagementSystem.exception.ReservationNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.ReservationCreateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final ParkingSessionRepository parkingSessionRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              VehicleRepository vehicleRepository,
                              SpotRepository spotRepository,
                              ParkingSessionRepository parkingSessionRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
    }

    @Transactional
    protected void expireOutdatedReservations() {
        LocalDateTime now = LocalDateTime.now();

        List<Reservation> outdated = reservationRepository
                .findByStatusAndEndTimeBefore(ReservationStatus.ACTIVE, now);

        if (outdated.isEmpty()) {
            return;
        }

        for (Reservation r : outdated) {
            r.setStatus(ReservationStatus.EXPIRED);
            r.setChanged(now);
        }

        reservationRepository.saveAll(outdated);

        log.info("Expired outdated reservations, count = {}", outdated.size());
    }

    public List<Reservation> getAllReservations() {
        log.info("Get all reservations");

        expireOutdatedReservations();

        List<Reservation> reservations = reservationRepository.findAll();
        log.info("Found {} reservations", reservations.size());

        return reservations;
    }

    public Reservation getReservationById(Long id) {
        log.info("Get reservation by id = {}", id);

        expireOutdatedReservations();

        return reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));
    }

    public Reservation createReservation(ReservationCreateDto dto) {
        log.info("Create reservation");
        log.debug("Create reservation payload = {}", dto);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(dto.getVehicleId()));

        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new SpotNotFoundException(dto.getSpotId()));

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            throw new ReservationConflictException(
                    "Spot with id = " + spot.getId() + " is not available for reservation"
            );
        }


        boolean hasOverlap = reservationRepository
                .existsBySpotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        spot.getId(),
                        ReservationStatus.ACTIVE,
                        dto.getEndTime(),
                        dto.getStartTime()
                );

        if (hasOverlap) {
            throw new ReservationConflictException(
                    "Spot with id = " + spot.getId() + " already has an active reservation in this time range"
            );
        }


        Reservation reservation = new Reservation(vehicle, spot, dto.getStartTime(), LocalDateTime.now());
        reservation.setEndTime(dto.getEndTime());
        reservation.setChanged(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation created, id = {}, vehicleId = {}, spotId = {}",
                saved.getId(), vehicle.getId(), spot.getId());

        return saved;
    }

    public Reservation updateReservation(Long id, ReservationUpdateDto dto) {
        log.info("Update reservation, id = {}", id);
        log.debug("Update reservation payload = {}", dto);

        expireOutdatedReservations();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (!dto.getEndTime().isAfter(reservation.getStartTime())) {
            throw new IllegalArgumentException("Reservation end time must be after start time");
        }

        boolean hasOverlap = reservationRepository
                .existsBySpotIdAndStatusAndIdNotAndStartTimeLessThanAndEndTimeGreaterThan(
                        reservation.getSpot().getId(),
                        ReservationStatus.ACTIVE,
                        reservation.getId(),
                        dto.getEndTime(),
                        reservation.getStartTime()
                );

        if (hasOverlap) {
            throw new ReservationConflictException(
                    "Updated reservation time overlaps with another active reservation for this spot"
            );
        }

        reservation.setEndTime(dto.getEndTime());
        reservation.setChanged(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation updated, id = {}", saved.getId());
        return saved;
    }

    public Reservation changeStatus(Long id, ReservationStatusUpdateDto dto) {
        log.info("Change reservation status, id = {}, status = {}", id, dto.getStatus());
        log.debug("Change reservation status payload = {}", dto);

        expireOutdatedReservations();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservation.setStatus(dto.getStatus());
        reservation.setChanged(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation status changed, id = {}", saved.getId());
        return saved;
    }

    public boolean deleteReservationById(Long id) {
        log.info("Delete reservation, id = {}", id);

        if (!reservationRepository.existsById(id)) {
            throw new ReservationNotFoundException(id);
        }

        if (parkingSessionRepository.existsByReservationId(id)) {
            throw new ReservationInUseException(id);
        }

        reservationRepository.deleteById(id);

        log.info("Reservation deleted, id = {}", id);
        return true;
    }

    public List<Reservation> getReservationsByVehicleId(Long vehicleId) {
        log.info("Get reservations by vehicleId = {}", vehicleId);

        expireOutdatedReservations();

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }

        List<Reservation> reservations = reservationRepository.findByVehicleId(vehicleId);
        log.info("Found {} reservations for vehicleId = {}", reservations.size(), vehicleId);

        return reservations;
    }

    public List<Reservation> getReservationsBySpotId(Long spotId) {
        log.info("Get reservations by spotId = {}", spotId);

        expireOutdatedReservations();

        if (!spotRepository.existsById(spotId)) {
            throw new SpotNotFoundException(spotId);
        }

        List<Reservation> reservations = reservationRepository.findBySpotId(spotId);
        log.info("Found {} reservations for spotId = {}", reservations.size(), spotId);

        return reservations;
    }
}
