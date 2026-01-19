package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.enums.Role;
import com.tms.ParkingManagementSystem.enums.UserStatus;
import com.tms.ParkingManagementSystem.exception.EmailAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.UserNotActiveException;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.dto.auth.AuthLoginDto;
import com.tms.ParkingManagementSystem.model.dto.auth.AuthRegisterDto;
import com.tms.ParkingManagementSystem.model.dto.auth.AuthResponseDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       SecurityRepository securityRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponseDto register(AuthRegisterDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        if (securityRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }

        User user = new User(LocalDateTime.now());
        user.setFirstName(dto.getFirstName());
        user.setSecondName(dto.getSecondName());
        user.setEmail(dto.getEmail());
        user.setDisabledPermit(dto.getDisabledPermit());
        user.setChanged(LocalDateTime.now());
        userRepository.save(user);

        Security sec = new Security();
        sec.setUsername(dto.getUsername());
        sec.setPassword(passwordEncoder.encode(dto.getPassword()));
        sec.setRole(Role.USER);
        sec.setUser(user);
        securityRepository.save(sec);

        String token = jwtService.generateToken(sec);

        return new AuthResponseDto(
                token
        );
    }


    public AuthResponseDto login(AuthLoginDto dto) {


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword())
        );

        Security sec = securityRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + dto.getUsername()));

        if (sec.getUser().getStatus() != UserStatus.ACTIVE) {
            throw new UserNotActiveException(sec.getUsername());
        }

        String token = jwtService.generateToken(sec);
        return new AuthResponseDto(token);
    }
}
