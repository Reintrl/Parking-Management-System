package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.dto.UserCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.UserStatusUpdateDto;
import com.tms.ParkingManagementSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        log.info("Request: get all users");
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) {
            log.warn("No users found");
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} users", users.size());
        return ResponseEntity.ok(users);
    }

    @PostMapping
    public ResponseEntity<User> createUser(
            @Valid @RequestBody UserCreateUpdateDto dto) {

        log.info("Request: create user");
        log.debug("Create user payload: {}", dto);
        User saved = userService.createUser(dto);

        log.info("User created id = {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        log.info("Request: get user by id = {}", id);
        User user = userService.getUserById(id);
        log.info("User found id = {}", id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserCreateUpdateDto dto) {

        log.info("Request: update user id = {}", id);
        log.debug("Update user payload: {}", dto);
        User updated = userService.updateUser(id, dto);

        log.info("User updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        log.info("Request: delete user id = {}", id);
        boolean deleted = userService.deleteUserById(id);
        if (deleted) {
            log.info("User deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to delete user id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<User> changeStatus(
            @PathVariable Long id,
            @Valid @RequestBody UserStatusUpdateDto dto) {

        log.info("Request: change user status id = {}", id);
        log.debug("Change status payload: {}", dto);
        User updated = userService.changeStatus(id, dto);
        log.info("User status updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/soft/{id}")
    public Boolean softDeleteUser(@PathVariable Long id) {
        return userService.softDeleteUser(id);
    }
}
