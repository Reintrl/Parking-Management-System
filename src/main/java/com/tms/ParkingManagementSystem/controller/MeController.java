package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.dto.UserCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.UserMeDto;
import com.tms.ParkingManagementSystem.model.dto.UserUpdateMeDto;
import com.tms.ParkingManagementSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/me")
public class MeController {

    private final UserService userService;

    public MeController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserMeDto> getMe() {
        log.info("Request: get current user profile");
        User me = userService.getCurrentUser();
        return ResponseEntity.ok(userService.mapToDto(me));
    }

    @PutMapping
    public ResponseEntity<UserMeDto> updateMe(
            @Valid @RequestBody UserUpdateMeDto dto) {

        log.info("Request: update current user profile");
        User updated = userService.updateCurrentUser(dto);
        return ResponseEntity.ok(userService.mapToDto(updated));
    }
}
