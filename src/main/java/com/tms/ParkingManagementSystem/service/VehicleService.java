package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.exception.PlateNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.UserNotActiveException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.VehicleInUseException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.VehicleCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import com.tms.ParkingManagementSystem.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ReservationRepository reservationRepository;
    private final SecurityUtil securityUtil;

    public VehicleService(VehicleRepository vehicleRepository,
                          UserRepository userRepository,
                          ParkingSessionRepository parkingSessionRepository,
                          ReservationRepository reservationRepository,
                          SecurityUtil securityUtil) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.reservationRepository = reservationRepository;
        this.securityUtil = securityUtil;
    }

    public List<Vehicle> getAllVehicles() {
        log.info("Get all vehicles");

        List<Vehicle> vehicles = vehicleRepository.findAll();
        log.info("Found {} vehicles", vehicles.size());

        return vehicles;
    }

    public Vehicle getVehicleById(Long id) {
        log.info("Get vehicle by id = {}", id);

        return securityUtil.assertVehicleOwnerOrAdmin(id);
    }

    @Transactional
    public Vehicle createVehicle(VehicleCreateUpdateDto dto) {
        log.info("Create vehicle");
        log.debug("Create vehicle payload = {}", dto);

        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User currentUser = securityUtil.getCurrentUser();

        if (!securityUtil.isAdmin() && !currentUser.getId().equals(dto.getUserId())) {
            throw new UserAccessDeniedException(dto.getUserId());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(user.getId());
        }

        Vehicle newVehicle = new Vehicle(LocalDateTime.now());
        newVehicle.setPlateNumber(dto.getPlateNumber());
        newVehicle.setType(dto.getType());
        newVehicle.setUser(user);
        newVehicle.setChanged(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(newVehicle);

        log.info("Vehicle created, id = {}, userId = {}, plateNumber = {}",
                saved.getId(), user.getId(), saved.getPlateNumber());

        return saved;
    }

    @Transactional
    public Vehicle updateVehicle(Long id, VehicleCreateUpdateDto dto) {
        log.info("Update vehicle, id = {}", id);
        log.debug("Update vehicle payload = {}", dto);

        Vehicle vehicleForUpdate = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            Long ownerId = vehicleForUpdate.getUser().getId();

            if (!ownerId.equals(currentUser.getId())) {
                throw new VehicleAccessDeniedException(id);
            }

            if (!currentUser.getId().equals(dto.getUserId())) {
                throw new UserAccessDeniedException(dto.getUserId());
            }
        }

        if (!dto.getPlateNumber().equals(vehicleForUpdate.getPlateNumber())
                && vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(user.getId());
        }

        vehicleForUpdate.setPlateNumber(dto.getPlateNumber());
        vehicleForUpdate.setType(dto.getType());
        vehicleForUpdate.setUser(user);
        vehicleForUpdate.setChanged(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicleForUpdate);

        log.info("Vehicle updated, id = {}, userId = {}, plateNumber = {}",
                saved.getId(), user.getId(), saved.getPlateNumber());

        return saved;
    }

    @Transactional
    public boolean deleteVehicleById(Long id) {
        log.info("Delete vehicle, id = {}", id);

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            Long ownerId = vehicle.getUser().getId();

            if (!ownerId.equals(currentUser.getId())) {
                throw new VehicleAccessDeniedException(id);
            }
        }

        if (parkingSessionRepository.existsByVehicleIdAndStatus(id, SessionStatus.ACTIVE)) {
            throw new VehicleInUseException(id, "There is an active parking session for this vehicle");
        }

        LocalDateTime now = LocalDateTime.now();
        if (reservationRepository.existsByVehicleIdAndStatusAndEndTimeAfter(id, ReservationStatus.ACTIVE, now)) {
            throw new VehicleInUseException(id, "There is an active (ongoing or future) reservation for this vehicle");
        }

        vehicleRepository.deleteById(id);

        log.info("Vehicle deleted, id = {}", id);
        return true;
    }


    public List<Vehicle> getAllVehicleByUserId(Long userId) {
        log.info("Get vehicles by userId = {}", userId);

        if (!securityUtil.isAdmin()) {
            User currentUser = securityUtil.getCurrentUser();
            if (!currentUser.getId().equals(userId)) {
                throw new UserAccessDeniedException(userId);
            }
        }

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Vehicle> vehicles = vehicleRepository.findAllByUserId(userId);
        log.info("Found {} vehicles for userId = {}", vehicles.size(), userId);

        return vehicles;
    }

}
