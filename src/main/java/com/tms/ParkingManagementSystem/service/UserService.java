package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.exception.EmailAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.dto.UserCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User createUser(UserCreateUpdateDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }
        User u = new User();
        u.setFirstName(dto.getFirstName());
        u.setSecondName(dto.getSecondName());
        u.setEmail(dto.getEmail());
        u.setDisabledPermit(dto.getDisabledPermit());
        u.setCreated(LocalDateTime.now());
        u.setChanged(LocalDateTime.now());
        return userRepository.save(u);
    }

    public User updateUser(Long id, UserCreateUpdateDto dto) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (!dto.getEmail().equals(u.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        u.setFirstName(dto.getFirstName());
        u.setSecondName(dto.getSecondName());
        u.setEmail(dto.getEmail());
        u.setDisabledPermit(dto.getDisabledPermit());
        u.setChanged(LocalDateTime.now());
        return userRepository.save(u);
    }


    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public boolean deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return false;
        }
        userRepository.deleteById(id);
        return true;
    }

    public User changeStatus(Long id, UserStatus status) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        user.setStatus(status);
        user.setChanged(LocalDateTime.now());
        return userRepository.save(user);
    }
}
