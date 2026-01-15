package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.exception.ReservationNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.ReservationCreateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ReservationUpdateDto;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final VehicleRepository vehicleRepository;
    private final SpotRepository spotRepository;

    public ReservationService(ReservationRepository reservationRepository,
                              VehicleRepository vehicleRepository,
                              SpotRepository spotRepository) {
        this.reservationRepository = reservationRepository;
        this.vehicleRepository = vehicleRepository;
        this.spotRepository = spotRepository;
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Optional<Reservation> getReservationById(Long id) {
        return reservationRepository.findById(id);
    }

    public Reservation createReservation(ReservationCreateDto dto) {
        Vehicle vehicle = vehicleRepository.findById(dto.getVehicleId())
                .orElseThrow(() -> new VehicleNotFoundException(dto.getVehicleId()));

        Spot spot = spotRepository.findById(dto.getSpotId())
                .orElseThrow(() -> new SpotNotFoundException(dto.getSpotId()));

        if (spot.getStatus() != SpotStatus.AVAILABLE) {
            throw new IllegalArgumentException(
                    "Spot with id=" + spot.getId() + " is not available for reservation"
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
            throw new IllegalArgumentException(
                    "Spot with id=" + spot.getId() + " already has an active reservation in this time range"
            );
        }

        Reservation reservation = new Reservation(
                vehicle,
                spot,
                dto.getStartTime(),
                LocalDateTime.now()
        );

        reservation.setEndTime(dto.getEndTime());
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation.setChanged(LocalDateTime.now());

        return reservationRepository.save(reservation);
    }

    public Reservation updateReservation(Long id, ReservationUpdateDto dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        if (dto.getEndTime() != null && reservation.getStartTime() != null
                && !dto.getEndTime().isAfter(reservation.getStartTime())) {
            throw new IllegalArgumentException("Reservation end time must be after start time");
        }

        reservation.setEndTime(dto.getEndTime());
        reservation.setChanged(LocalDateTime.now());

        boolean hasOverlap = reservationRepository
                .existsBySpotIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        reservation.getSpot().getId(),
                        ReservationStatus.ACTIVE,
                        dto.getEndTime(),
                        reservation.getStartTime()
                );

        if (hasOverlap) {
            throw new IllegalArgumentException(
                    "Updated reservation time overlaps with another active reservation for this spot"
            );
        }

        return reservationRepository.save(reservation);
    }

    public Reservation changeStatus(Long id, ReservationStatusUpdateDto dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationNotFoundException(id));

        reservation.setStatus(dto.getStatus());
        reservation.setChanged(LocalDateTime.now());
        return reservationRepository.save(reservation);
    }

    public boolean deleteReservationById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new ReservationNotFoundException(id);
        }
        reservationRepository.deleteById(id);
        return true;
    }

    public List<Reservation> getReservationsByVehicleId(Long vehicleId) {
        if (!vehicleRepository.existsById(vehicleId)) {
            throw new VehicleNotFoundException(vehicleId);
        }
        return reservationRepository.findByVehicleId(vehicleId);
    }

    public List<Reservation> getReservationsBySpotId(Long spotId) {
        if (!spotRepository.existsById(spotId)) {
            throw new SpotNotFoundException(spotId);
        }
        return reservationRepository.findBySpotId(spotId);
    }
}
