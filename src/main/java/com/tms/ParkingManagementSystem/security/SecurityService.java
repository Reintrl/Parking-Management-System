package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.exception.SecurityNotFoundException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.UsernameAlreadyExistsException;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.dto.SecurityUpdateDto;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityService(UserRepository userRepository,
                           SecurityRepository securityRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<Security> getSecurityById(Long id) {
        return securityRepository.findById(id);
    }

    public Boolean setRoleToAdmin(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return securityRepository.setAdminRoleByUserId(id) > 0;
    }

    public List<Security> getAllSecuritiesByRole(String role) {
        return securityRepository.customFindByRole(role);
    }

    public Boolean setRoleToOperator(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return securityRepository.setOperatorRoleByUserId(id) > 0;
    }

    public List<Security> getAllSecurities() {
        return securityRepository.findAll();
    }

    public Boolean setRoleToUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        return securityRepository.setUserRoleByUserId(id) > 0;
    }

    public Security updateSecurity(Long id, SecurityUpdateDto dto) {
        log.info("Update security, id = {}", id);
        log.debug("Update security payload = {}", dto);

        Security security = securityRepository.findById(id)
                .orElseThrow(() -> new SecurityNotFoundException(id));

        if (securityRepository.existsByUsernameAndIdNot(dto.getUsername(), id)) {
            throw new UsernameAlreadyExistsException(dto.getUsername());
        }

        security.setUsername(dto.getUsername());
        security.setPassword(passwordEncoder.encode(dto.getPassword()));

        Security saved = securityRepository.save(security);

        log.info("Security updated, id = {}, username = {}", saved.getId(), saved.getUsername());
        return saved;
    }
}
