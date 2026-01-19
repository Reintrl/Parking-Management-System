package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.exception.EmailAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.UserInUseException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.model.dto.UserCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.UserMeDto;
import com.tms.ParkingManagementSystem.model.dto.UserStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.UserUpdateMeDto;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import com.tms.ParkingManagementSystem.security.SecurityRepository;
import com.tms.ParkingManagementSystem.security.SecurityUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ReservationRepository reservationRepository;
    private final SecurityRepository securityRepository;
    private final SecurityUtil securityUtil;

    public UserService(UserRepository userRepository,
                       VehicleRepository vehicleRepository,
                       ParkingSessionRepository parkingSessionRepository,
                       ReservationRepository reservationRepository,
                       SecurityRepository securityRepository,
                       SecurityUtil securityUtil) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.reservationRepository = reservationRepository;
        this.securityRepository = securityRepository;
        this.securityUtil = securityUtil;
    }

    public List<User> getAllUsers() {
        log.info("Get all users");

        List<User> users = userRepository.findAll();
        log.info("Found {} users", users.size());

        return users;
    }

    @Transactional
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

    @Transactional
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

    @Transactional
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

    public boolean softDeleteUser(Long id) {
        log.info("Soft delete user, id = {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        User currentUser = getCurrentUser();

        if (!securityUtil.isAdmin() && !currentUser.getId().equals(user.getId())) {
            throw new UserAccessDeniedException(id);
        }

        if (user.getStatus() == UserStatus.DELETED) {
            log.warn("User already DELETED, id = {}", id);
            return false;
        }

        user.setStatus(UserStatus.DELETED);
        user.setChanged(LocalDateTime.now());

        userRepository.save(user);

        log.info("User soft deleted, id = {}", id);
        return true;
    }

    public User getCurrentUser() {
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Security> userSecurity = securityRepository.findByUsername(userLogin);
        if (userSecurity.isPresent()) {
            return userRepository.findById(userSecurity.get().getUser().getId()).get();
        }
        throw new UserNotFoundException(userSecurity.get().getUser().getId());
    }

    public User updateCurrentUser(UserUpdateMeDto dto) {
        User user = getCurrentUser();
        user.setFirstName(dto.getFirstName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setChanged(LocalDateTime.now());

        return userRepository.save(user);
    }

    public UserMeDto mapToDto(User user) {
        UserMeDto dto = new UserMeDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setSecondName(user.getSecondName());
        dto.setDisabledPermit(user.getDisabledPermit());
        dto.setStatus(user.getStatus());
        return dto;
    }
}
