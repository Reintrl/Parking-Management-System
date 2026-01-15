package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.exception.PlateNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.VehicleCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        return vehicle;
    }

    public Vehicle createVehicle(VehicleCreateUpdateDto dto) {
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        Vehicle newVehicle = new Vehicle();
        newVehicle.setPlateNumber(dto.getPlateNumber());
        newVehicle.setType(dto.getType());
        newVehicle.setUser(user);
        newVehicle.setCreated(LocalDateTime.now());
        newVehicle.setChanged(LocalDateTime.now());

        return vehicleRepository.save(newVehicle);
    }

    public Vehicle updateVehicle(Long id, VehicleCreateUpdateDto dto) {
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

        return vehicleRepository.save(vehicleForUpdate);
    }

    public boolean deleteVehicleById(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException(id);
        }
        vehicleRepository.deleteById(id);
        return true;
    }


    public List<Vehicle> getAllVehicleByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return vehicleRepository.findAllByUserId(userId);
    }
}