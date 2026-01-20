package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityService {
    private final UserRepository userRepository;
    private final SecurityRepository securityRepository;

    public SecurityService(UserRepository userRepository, SecurityRepository securityRepository) {
        this.userRepository = userRepository;
        this.securityRepository = securityRepository;
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
}
