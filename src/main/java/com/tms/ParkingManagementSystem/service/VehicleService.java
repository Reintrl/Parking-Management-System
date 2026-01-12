package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.exception.PlateNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.VehicleCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.VehicleResponseDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public VehicleService(VehicleRepository vehicleRepository,
                          UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public List<VehicleResponseDto> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    public VehicleResponseDto getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
        return toDto(vehicle);
    }

    public VehicleResponseDto createVehicle(VehicleCreateUpdateDto dto) {
        if (vehicleRepository.existsByPlateNumber(dto.getPlateNumber())) {
            throw new PlateNumberAlreadyExistsException(dto.getPlateNumber());
        }

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new UserNotFoundException(dto.getUserId()));

        Vehicle newVehicle = new Vehicle();
        newVehicle.setPlateNumber(dto.getPlateNumber());
        newVehicle.setType(dto.getType());
        newVehicle.setUser(user);

        return toDto(vehicleRepository.save(newVehicle));
    }

    public VehicleResponseDto updateVehicle(Long id, VehicleCreateUpdateDto dto) {
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

        return toDto(vehicleRepository.save(vehicleForUpdate));
    }

    public boolean deleteVehicleById(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException(id);
        }
        vehicleRepository.deleteById(id);
        return true;
    }

    private VehicleResponseDto toDto(Vehicle vehicle) {
        return new VehicleResponseDto(
                vehicle.getId(),
                vehicle.getPlateNumber(),
                vehicle.getType(),
                vehicle.getUser().getId()
        );
    }

    public List<VehicleResponseDto> getAllVehicleByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return vehicleRepository.findAllByUserId(userId)
                .stream()
                .map(this::toDto)
                .toList();
    }


}