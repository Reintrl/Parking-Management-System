package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.model.dto.auth.AuthLoginDto;
import com.tms.ParkingManagementSystem.model.dto.auth.AuthRegisterDto;
import com.tms.ParkingManagementSystem.model.dto.auth.AuthResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Valid @RequestBody AuthRegisterDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody AuthLoginDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
