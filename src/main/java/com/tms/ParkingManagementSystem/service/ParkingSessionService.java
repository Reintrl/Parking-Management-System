package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.exception.ParkingSessionConflictException;
import com.tms.ParkingManagementSystem.exception.ParkingSessionNotFoundException;
import com.tms.ParkingManagementSystem.exception.ReservationNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.ParkingSessionCreateDto;
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
public class ParkingSessionService {

    private final ParkingSessionRepository parkingSessionRepository;
    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;
    private final ReservationRepository reservationRepository;

    public ParkingSessionService(ParkingSessionRepository parkingSessionRepository,
                                 VehicleRepository vehicleRepository,
                                 SpotRepository spotRepository,
                                 ReservationRepository reservationRepository) {
        this.parkingSessionRepository = parkingSessionRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ParkingSession> getAllSessions() {
        log.info("Get all parking sessions");
        return parkingSessionRepository.findAll();
    }

    public ParkingSession getSessionById(Long id) {
        log.info("Get parking session by id = {}", id);

        return parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ParkingSessionNotFoundException(id));
    }

    @Transactional
    public ParkingSession createSession(ParkingSessionCreateDto dto) {
        log.info("Create parking session");
        log.debug("Create parking session payload = {}", dto);

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(dto.getVehicleId()));

        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new SpotNotFoundException(dto.getSpotId()));

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            throw new ParkingSessionConflictException(
                    "Spot with id = " + spot.getId() + " is not available for starting a parking session"
            );
        }

        if (parkingSessionRepository.existsBySpotIdAndStatus(spot.getId(), SessionStatus.ACTIVE)) {
            throw new ParkingSessionConflictException(
                    "Active parking session already exists for spot id = " + spot.getId()
            );
        }

        if (parkingSessionRepository.existsByVehicleIdAndStatus(vehicle.getId(), SessionStatus.ACTIVE)) {
            throw new ParkingSessionConflictException(
                    "Active parking session already exists for vehicle id = " + vehicle.getId()
            );
        }

        ParkingSession session = new ParkingSession(vehicle, spot, LocalDateTime.now());

        if (dto.getReservationId() != null) {
            Long reservationId = dto.getReservationId();

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));

            if (parkingSessionRepository.existsByReservationId(reservation.getId())) {
                throw new ParkingSessionConflictException(
                        "Reservation with id = " + reservation.getId() + " is already used by a parking session"
                );
            }

            if (reservation.getStatus() != ReservationStatus.ACTIVE) {
                throw new ParkingSessionConflictException(
                        "Reservation with id = " + reservation.getId() + " is not ACTIVE"
                );
            }

            if (!reservation.getVehicle().getId().equals(vehicle.getId())) {
                throw new ParkingSessionConflictException("Reservation vehicle does not match requested vehicle");
            }

            if (!reservation.getSpot().getId().equals(spot.getId())) {
                throw new ParkingSessionConflictException("Reservation spot does not match requested spot");
            }

            LocalDateTime now = LocalDateTime.now();
            if (now.isBefore(reservation.getStartTime()) || !now.isBefore(reservation.getEndTime())) {
                throw new ParkingSessionConflictException(
                        "Reservation time window is not valid for starting a session now"
                );
            }

            session.setReservation(reservation);
            log.info("Reservation linked to parking session, reservationId = {}", reservation.getId());
        }

        spot.setStatus(SpotStatus.OCCUPIED);
        spot.setChanged(LocalDateTime.now());
        spotRepository.save(spot);

        log.info("Spot marked as OCCUPIED, spotId = {}", spot.getId());

        ParkingSession saved = parkingSessionRepository.save(session);

        log.info("Parking session created, id = {}, vehicleId = {}, spotId = {}",
                saved.getId(), vehicle.getId(), spot.getId());

        return saved;
    }

    public ParkingSession createSessionFromReservation(Long reservationId) {
        log.info("Create parking session from reservation, reservationId = {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException(reservationId));

        ParkingSessionCreateDto dto = new ParkingSessionCreateDto();
        dto.setReservationId(reservation.getId());
        dto.setVehicleId(reservation.getVehicle().getId());
        dto.setSpotId(reservation.getSpot().getId());

        return createSession(dto);
    }

    @Transactional
    public boolean deleteSessionById(Long id) {
        log.info("Delete parking session, id = {}", id);

        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ParkingSessionNotFoundException(id));

        if (session.getStatus() == SessionStatus.ACTIVE) {
            throw new ParkingSessionConflictException(
                    "Cannot delete ACTIVE parking session. Finish it first."
            );
        }

        parkingSessionRepository.deleteById(id);

        log.info("Parking session deleted, id = {}", id);
        return true;
    }

    public List<ParkingSession> getSessionsBySpotId(Long spotId) {
        log.info("Get parking sessions by spotId = {}", spotId);

        if (!spotRepository.existsById(spotId)) {
            throw new SpotNotFoundException(spotId);
        }

        return parkingSessionRepository.findBySpotId(spotId);
    }

    public List<ParkingSession> getSessionsByVehicleId(Long vehicleId) {
        log.info("Get parking sessions by vehicleId = {}", vehicleId);

        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }

        return parkingSessionRepository.findByVehicleId(vehicleId);
    }

    @Transactional
    public ParkingSession finishSession(Long id) {
        log.info("Finish parking session, id = {}", id);

        ParkingSession session = parkingSessionRepository.findById(id)
                .orElseThrow(() -> new ParkingSessionNotFoundException(id));

        if (session.getStatus() == SessionStatus.FINISHED) {
            throw new ParkingSessionConflictException("Parking session with id = " + id + " is already FINISHED");
        }

        session.setStatus(SessionStatus.FINISHED);
        session.setEndTime(LocalDateTime.now());

        Spot spot = session.getSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        spot.setChanged(LocalDateTime.now());
        spotRepository.save(spot);

        log.info("Spot marked as AVAILABLE, spotId = {}", spot.getId());

        ParkingSession saved = parkingSessionRepository.save(session);

        log.info("Parking session finished, id = {}", saved.getId());

        return saved;
    }
}
