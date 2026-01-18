package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.exception.EmailAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserInUseException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.UserCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.UserStatusUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ReservationRepository reservationRepository;


    public UserService(UserRepository userRepository,
                       VehicleRepository vehicleRepository,
                       ParkingSessionRepository parkingSessionRepository,
                       ReservationRepository reservationRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<User> getAllUsers() {
        log.info("Get all users");

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());

        return users;
    }

    public User createUser(UserCreateUpdateDto dto) {
        log.info("Create user");
        log.debug("Create user payload = {}", dto);

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = new User(LocalDateTime.now());
        user.setFirstName(dto.getFirstName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setDisabledPermit(dto.getDisabledPermit());
        user.setChanged(LocalDateTime.now());

        User saved = userRepository.save(user);

        log.info("User created, id = {}, email = {}", saved.getId(), saved.getEmail());
        return saved;
    }

    public User updateUser(Long id, UserCreateUpdateDto dto) {
        log.info("Update user, id = {}", id);
        log.debug("Update user payload = {}", dto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!dto.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        user.setFirstName(dto.getFirstName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setDisabledPermit(dto.getDisabledPermit());
        user.setChanged(LocalDateTime.now());

        User saved = userRepository.save(user);

        log.info("User updated, id = {}, email = {}", saved.getId(), saved.getEmail());
        return saved;
    }

    public User getUserById(Long id) {
        log.info("Get user by id = {}", id);

        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public boolean deleteUserById(Long userId) {
        log.info("Delete user, id = {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        validateUserInUse(userId);

        vehicleRepository.deleteAllByUserId(userId);
        userRepository.deleteById(userId);

        log.info("User deleted, id = {}", userId);
        return true;
    }

    public User changeStatus(Long id, UserStatusUpdateDto dto) {
        log.info("Change user status, id = {}, status = {}", id, dto.getStatus());
        log.debug("Change user status payload = {}", dto);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (dto.getStatus().equals(UserStatus.BLOCKED)) {
            validateUserInUse(id);
        }

        user.setStatus(dto.getStatus());
        user.setChanged(LocalDateTime.now());

        User saved = userRepository.save(user);

        log.info("User status changed, id = {}", saved.getId());
        return saved;
    }

    private void validateUserInUse(Long id) {
        List<Vehicle> vehicles = vehicleRepository.findAllByUserId(id);
        LocalDateTime now = LocalDateTime.now();

        for (Vehicle v : vehicles) {
            Long vehicleId = v.getId();

            if (parkingSessionRepository.existsByVehicleIdAndStatus(vehicleId, SessionStatus.ACTIVE)) {
                throw new UserInUseException(id,
                        "Vehicle id = " + vehicleId + " has an active parking session");
            }

            if (reservationRepository.existsByVehicleIdAndStatusAndEndTimeAfter(vehicleId, ReservationStatus.ACTIVE, now)) {
                throw new UserInUseException(id,
                        "Vehicle id = " + vehicleId + " has an active (ongoing or future) reservation");
            }
        }
    }
}
