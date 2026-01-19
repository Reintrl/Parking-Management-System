package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.enums.VehicleType;
import com.tms.ParkingManagementSystem.exception.ReservationConflictException;
import com.tms.ParkingManagementSystem.exception.ReservationInUseException;
import com.tms.ParkingManagementSystem.exception.ReservationNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.ReservationCreateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import com.tms.ParkingManagementSystem.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SpotRepository spotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final SecurityUtil securityUtil;

    public ReservationService(ReservationRepository reservationRepository,
                              SpotRepository spotRepository,
                              ParkingSessionRepository parkingSessionRepository,
                              SecurityUtil securityUtil) {
        this.reservationRepository = reservationRepository;
        this.spotRepository = spotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.securityUtil = securityUtil;
    }

    private void expireOutdatedReservations() {
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

    private void validateSpotTypeForVehicle(Spot spot, Vehicle vehicle) {
        SpotType spotType = spot.getType();
        VehicleType vehicleType = vehicle.getType();

        log.debug(
                "Validate spot type for vehicle, spotId = {}, spotType = {}, vehicleId = {}, vehicleType = {}",
                spot.getId(), spotType, vehicle.getId(), vehicleType
        );

        if (spotType == SpotType.ELECTRIC && vehicleType != VehicleType.ELECTRIC_CAR) {
            log.warn(
                    "Reservation denied: ELECTRIC spot requires ELECTRIC_CAR, spotId = {}, vehicleId = {}, vehicleType = {}",
                    spot.getId(), vehicle.getId(), vehicleType
            );
            throw new ReservationConflictException(
                    "Spot id = " + spot.getId() + " is ELECTRIC, only ELECTRIC_CAR is allowed"
            );
        }

        if (spotType == SpotType.TRUCK && vehicleType != VehicleType.TRUCK) {
            log.warn(
                    "Reservation denied: TRUCK spot used by non-TRUCK vehicle, spotId = {}, vehicleId = {}, vehicleType = {}",
                    spot.getId(), vehicle.getId(), vehicleType
            );
            throw new ReservationConflictException(
                    "Spot id = " + spot.getId() + " is for TRUCK, vehicle type = " + vehicleType
            );
        }
    }

    private void validateDisabledPermit(Spot spot, Vehicle vehicle) {
        User user = vehicle.getUser();

        log.debug(
                "Validate disabled permit, spotId = {}, spotType = {}, userId = {}, disabledPermit = {}",
                spot.getId(), spot.getType(), user.getId(), user.getDisabledPermit()
        );

        if (spot.getType() == SpotType.DISABLED && !user.getDisabledPermit()) {
            log.warn(
                    "Reservation denied: disabled spot requires permit, spotId = {}, userId = {}",
                    spot.getId(), user.getId()
            );
            throw new ReservationConflictException(
                    "Spot id = " + spot.getId()
                            + " is for disabled drivers, user id = "
                            + user.getId()
                            + " has no disabled permit"
            );
        }
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

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        securityUtil.assertReservationOwnerOrAdmin(reservation);

        return reservation;
    }


    @Transactional
    public Reservation createReservation(ReservationCreateDto dto) {
        log.info("Create reservation");
        log.debug("Create reservation payload = {}", dto);

        LocalDateTime now = LocalDateTime.now();

        if (!dto.getEndTime().isAfter(dto.getStartTime())) {
            log.warn("Reservation denied: endTime must be after startTime, start = {}, end = {}",
                    dto.getStartTime(), dto.getEndTime());
            throw new IllegalArgumentException("Reservation end time must be after start time");
        }

        Vehicle vehicle = securityUtil.assertVehicleOwnerOrAdmin(dto.getVehicleId());

        if (parkingSessionRepository.existsByVehicleIdAndStatus(vehicle.getId(), SessionStatus.ACTIVE)) {
            throw new ReservationConflictException(
                    "Vehicle id = " + vehicle.getId() + " already has an active parking session"
            );
        }

        User user = vehicle.getUser();
        log.debug("Reservation vehicle found, vehicleId = {}, userId = {}", vehicle.getId(), user.getId());

        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("Reservation denied: user is not ACTIVE, userId = {}, status = {}",
                    user.getId(), user.getStatus());
            throw new ReservationConflictException(
                    "User with id = " + user.getId() + " is not ACTIVE"
            );
        }

        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new SpotNotFoundException(dto.getSpotId()));

        log.debug("Reservation spot found, spotId = {}, status = {}", spot.getId(), spot.getStatus());

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            log.warn("Reservation denied: spot not AVAILABLE, spotId = {}, status = {}",
                    spot.getId(), spot.getStatus());
            throw new ReservationConflictException(
                    "Spot with id = " + spot.getId() + " is not available for reservation"
            );
        }

        validateSpotTypeForVehicle(spot, vehicle);
        validateDisabledPermit(spot, vehicle);

        boolean hasOverlap = reservationRepository
                .existsBySpotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        spot.getId(),
                        ReservationStatus.ACTIVE,
                        dto.getEndTime(),
                        dto.getStartTime()
                );

        if (hasOverlap) {
            log.warn("Reservation denied: time overlap, spotId = {}, start = {}, end = {}",
                    spot.getId(), dto.getStartTime(), dto.getEndTime());
            throw new ReservationConflictException(
                    "Spot with id = " + spot.getId() + " already has an active reservation in this time range"
            );
        }

        Reservation reservation = new Reservation(vehicle, spot, dto.getStartTime(), now);
        reservation.setEndTime(dto.getEndTime());
        reservation.setChanged(now);

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation created, id = {}, vehicleId = {}, spotId = {}",
                saved.getId(), vehicle.getId(), spot.getId());

        return saved;
    }

    @Transactional
    public Reservation updateReservation(Long id, ReservationUpdateDto dto) {
        log.info("Update reservation, id = {}", id);
        log.debug("Update reservation payload = {}", dto);

        expireOutdatedReservations();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));


        securityUtil.assertReservationOwnerOrAdmin(reservation);

        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            log.warn("Reservation update denied: not ACTIVE, id = {}, status = {}",
                    reservation.getId(), reservation.getStatus());
            throw new ReservationConflictException("Only ACTIVE reservations can be updated");
        }

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
            log.warn("Reservation update denied: time overlap, reservationId = {}", reservation.getId());
            throw new ReservationConflictException(
                    "Updated reservation time overlaps with another active reservation for this spot"
            );
        }

        if (parkingSessionRepository.existsByReservationId(reservation.getId())) {
            throw new ReservationConflictException(
                    "Cannot update reservation already used by a parking session, id = " + reservation.getId()
            );
        }

        reservation.setEndTime(dto.getEndTime());
        reservation.setChanged(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation updated, id = {}", saved.getId());
        return saved;
    }


    @Transactional
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

    @Transactional
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

        securityUtil.assertVehicleOwnerOrAdmin(vehicleId);
        expireOutdatedReservations();

        return reservationRepository.findByVehicleId(vehicleId);
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

    @Transactional
    public Reservation cancelReservation(Long id) {
        log.info("Cancel reservation, id = {}", id);

        expireOutdatedReservations();

        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        securityUtil.assertReservationOwnerOrAdmin(reservation);

        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            return reservation;
        }

        if (reservation.getStatus() == ReservationStatus.EXPIRED) {
            throw new ReservationConflictException("Cannot cancel EXPIRED reservation, id = " + id);
        }

        if (parkingSessionRepository.existsByReservationId(reservation.getId())) {
            throw new ReservationConflictException("Cannot cancel reservation already used by a parking session, id = " + id);
        }

        LocalDateTime now = LocalDateTime.now();
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setChanged(now);

        Reservation saved = reservationRepository.save(reservation);

        log.info("Reservation cancelled, id = {}", id);
        return saved;
    }

}
