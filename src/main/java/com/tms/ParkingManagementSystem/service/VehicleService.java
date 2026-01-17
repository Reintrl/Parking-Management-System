package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.exception.PlateNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.VehicleCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public List<Vehicle> getAllVehicles() {
        log.info("Get all vehicles");

        List<Vehicle> vehicles = vehicleRepository.findAll();
        log.info("Found {} vehicles", vehicles.size());

        return vehicles;
    }

    public Vehicle getVehicleById(Long id) {
        log.info("Get vehicle by id = {}", id);

        return vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    public Vehicle createVehicle(VehicleCreateUpdateDto dto) {
        log.info("Create vehicle");
        log.debug("Create vehicle payload = {}", dto);

        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

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

    public Vehicle updateVehicle(Long id, VehicleCreateUpdateDto dto) {
        log.info("Update vehicle, id = {}", id);
        log.debug("Update vehicle payload = {}", dto);

        Vehicle vehicleForUpdate = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));

        if (!dto.getPlateNumber().equals(vehicleForUpdate.getPlateNumber())
                && vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        vehicleForUpdate.setPlateNumber(dto.getPlateNumber());
        vehicleForUpdate.setType(dto.getType());
        vehicleForUpdate.setUser(user);
        vehicleForUpdate.setChanged(LocalDateTime.now());

        Vehicle saved = vehicleRepository.save(vehicleForUpdate);

        log.info("Vehicle updated, id = {}, userId = {}, plateNumber = {}",
                saved.getId(), user.getId(), saved.getPlateNumber());

        return saved;
    }

    public boolean deleteVehicleById(Long id) {
        log.info("Delete vehicle, id = {}", id);

        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException(id);
        }

        vehicleRepository.deleteById(id);

        log.info("Vehicle deleted, id = {}", id);
        return true;
    }

    public List<Vehicle> getAllVehicleByUserId(Long userId) {
        log.info("Get vehicles by userId = {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        List<Vehicle> vehicles = vehicleRepository.findAllByUserId(userId);
        log.info("Found {} vehicles for userId = {}", vehicles.size(), userId);

        return vehicles;
    }
}
