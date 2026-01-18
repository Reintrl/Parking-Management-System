package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.enums.VehicleType;
import com.tms.ParkingManagementSystem.exception.ParkingSessionConflictException;
import com.tms.ParkingManagementSystem.exception.ParkingSessionNotFoundException;
import com.tms.ParkingManagementSystem.exception.ReservationNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.User;
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

        LocalDateTime now = LocalDateTime.now();

        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(dto.getVehicleId()));

        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new SpotNotFoundException(dto.getSpotId()));

        log.debug("Session request resolved: vehicleId = {}, spotId = {}", vehicle.getId(), spot.getId());

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            log.warn("Session denied: spot not AVAILABLE, spotId = {}, status = {}", spot.getId(), spot.getStatus());
            throw new ParkingSessionConflictException(
                    "Spot with id = " + spot.getId() + " is not available for starting a parking session"
            );
        }

        if (parkingSessionRepository.existsBySpotIdAndStatus(spot.getId(), SessionStatus.ACTIVE)) {
            log.warn("Session denied: active session exists for spot, spotId = {}", spot.getId());
            throw new ParkingSessionConflictException(
                    "Active parking session already exists for spot id = " + spot.getId()
            );
        }

        if (parkingSessionRepository.existsByVehicleIdAndStatus(vehicle.getId(), SessionStatus.ACTIVE)) {
            log.warn("Session denied: active session exists for vehicle, vehicleId = {}", vehicle.getId());
            throw new ParkingSessionConflictException(
                    "Active parking session already exists for vehicle id = " + vehicle.getId()
            );
        }

        if (dto.getReservationId() == null) {
            validateUserAndSpotRulesForSession(vehicle, spot);

            boolean reservedNow = reservationRepository
                    .existsBySpotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                            spot.getId(),
                            ReservationStatus.ACTIVE,
                            now,
                            now
                    );

            if (reservedNow) {
                log.warn("Session denied: spot has active reservation for current time, spotId = {}", spot.getId());
                throw new ParkingSessionConflictException(
                        "Spot id = " + spot.getId() + " has an active reservation for the current time"
                );
            }
        }

        ParkingSession session = new ParkingSession(vehicle, spot, now);

        if (dto.getReservationId() != null) {
            Long reservationId = dto.getReservationId();

            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new ReservationNotFoundException(reservationId));

            if (parkingSessionRepository.existsByReservationId(reservation.getId())) {
                log.warn("Session denied: reservation already used by a session, reservationId = {}", reservation.getId());
                throw new ParkingSessionConflictException(
                        "Reservation with id = " + reservation.getId() + " is already used by a parking session"
                );
            }

            if (reservation.getStatus() != ReservationStatus.ACTIVE) {
                log.warn("Session denied: reservation not ACTIVE, reservationId = {}, status = {}",
                        reservation.getId(), reservation.getStatus());
                throw new ParkingSessionConflictException(
                        "Reservation with id = " + reservation.getId() + " is not ACTIVE"
                );
            }

            if (!reservation.getVehicle().getId().equals(vehicle.getId())) {
                log.warn("Session denied: reservation vehicle mismatch, reservationId = {}, requestedVehicleId = {}, reservationVehicleId = {}",
                        reservation.getId(), vehicle.getId(), reservation.getVehicle().getId());
                throw new ParkingSessionConflictException("Reservation vehicle does not match requested vehicle");
            }

            if (!reservation.getSpot().getId().equals(spot.getId())) {
                log.warn("Session denied: reservation spot mismatch, reservationId = {}, requestedSpotId = {}, reservationSpotId = {}",
                        reservation.getId(), spot.getId(), reservation.getSpot().getId());
                throw new ParkingSessionConflictException("Reservation spot does not match requested spot");
            }

            if (now.isBefore(reservation.getStartTime()) || !now.isBefore(reservation.getEndTime())) {
                log.warn("Session denied: outside reservation time window, reservationId = {}, now = {}, start = {}, end = {}",
                        reservation.getId(), now, reservation.getStartTime(), reservation.getEndTime());
                throw new ParkingSessionConflictException(
                        "Reservation time window is not valid for starting a session now"
                );
            }

            session.setReservation(reservation);
            log.info("Reservation linked to parking session, reservationId = {}", reservation.getId());
        }

        spot.setStatus(SpotStatus.OCCUPIED);
        spot.setChanged(now);
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
            log.warn("Session delete denied: session is ACTIVE, id = {}", id);
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
            log.warn("Session finish denied: already FINISHED, id = {}", id);
            throw new ParkingSessionConflictException("Parking session with id = " + id + " is already FINISHED");
        }

        LocalDateTime now = LocalDateTime.now();

        session.setStatus(SessionStatus.FINISHED);
        session.setEndTime(now);

        Spot spot = session.getSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        spot.setChanged(now);
        spotRepository.save(spot);

        Reservation reservation = session.getReservation();
        if(reservation != null) {
            reservation.setStatus(ReservationStatus.EXPIRED);
            reservation.setChanged(now);
            reservationRepository.save(reservation);
        }

        log.info("Spot marked as AVAILABLE, spotId = {}", spot.getId());

        ParkingSession saved = parkingSessionRepository.save(session);

        log.info("Parking session finished, id = {}", saved.getId());

        return saved;
    }

    private void validateUserAndSpotRulesForSession(Vehicle vehicle, Spot spot) {
        User user = vehicle.getUser();

        log.debug("Validate user status for session, userId = {}, status = {}", user.getId(), user.getStatus());
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Session denied: user is not ACTIVE, userId = {}, status = {}", user.getId(), user.getStatus());
            throw new ParkingSessionConflictException("User with id = " + user.getId() + " is not ACTIVE");
        }

        SpotType spotType = spot.getType();
        VehicleType vehicleType = vehicle.getType();

        log.debug("Validate spot type for session, spotId = {}, spotType = {}, vehicleId = {}, vehicleType = {}",
                spot.getId(), spotType, vehicle.getId(), vehicleType);

        if (spotType == SpotType.ELECTRIC && vehicleType != VehicleType.ELECTRIC_CAR) {
            log.warn("Session denied: ELECTRIC spot requires ELECTRIC_CAR, spotId = {}, vehicleId = {}, vehicleType = {}",
                    spot.getId(), vehicle.getId(), vehicleType);
            throw new ParkingSessionConflictException(
                    "Spot id = " + spot.getId() + " is ELECTRIC, only ELECTRIC_CAR is allowed"
            );
        }

        if (spotType == SpotType.TRUCK && vehicleType != VehicleType.TRUCK) {
            log.warn("Session denied: TRUCK spot used by non-TRUCK vehicle, spotId = {}, vehicleId = {}, vehicleType = {}",
                    spot.getId(), vehicle.getId(), vehicleType);
            throw new ParkingSessionConflictException(
                    "Spot id = " + spot.getId() + " is for TRUCK, vehicle type = " + vehicleType
            );
        }

        log.debug("Validate disabled permit for session, spotId = {}, spotType = {}, userId = {}, disabledPermit = {}",
                spot.getId(), spotType, user.getId(), user.getDisabledPermit());

        if (spotType == SpotType.DISABLED && !user.getDisabledPermit()) {
            log.warn("Session denied: disabled spot requires permit, spotId = {}, userId = {}", spot.getId(), user.getId());
            throw new ParkingSessionConflictException(
                    "Spot id = " + spot.getId()
                            + " is for disabled drivers, user id = " + user.getId()
                            + " has no disabled permit"
            );
        }
    }
}
