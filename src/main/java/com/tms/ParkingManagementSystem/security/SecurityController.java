package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.enums.Role;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/security")
public class SecurityController {

    public SecurityService securityService;
    public UserService userService;

    public SecurityController(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Security> getSecurityById(@PathVariable("id") Long id) {
        Optional<Security> security = securityService.getSecurityById(id);
        if (security.isPresent()) {
            return new ResponseEntity<>(security.get(), HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<Security>> getAllSecuritiesByRole(@PathVariable("role") String role) {
        try {
            role = role.toUpperCase();
            Role.valueOf(role);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        List<Security> allSecuritiesByRole = securityService.getAllSecuritiesByRole(role);
        if (!allSecuritiesByRole.isEmpty()) {
            return new ResponseEntity<>(allSecuritiesByRole, HttpStatus.OK);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/{id}/admin")
    public ResponseEntity<HttpStatusCode> setRoleToAdmin(@PathVariable Long id) {
        if (securityService.setRoleToAdmin(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/{id}/operator")
    public ResponseEntity<HttpStatusCode> setRoleToOperator(@PathVariable Long id) {
        if (securityService.setRoleToOperator(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @GetMapping
    public ResponseEntity<List<Security>> getAllSecurity() {
        List<Security> securities = securityService.getAllSecurities();
        if (securities.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(securities);
    }

    @PostMapping("/{id}/user")
    public ResponseEntity<HttpStatusCode> setRoleToUser(@PathVariable Long id) {
        if (securityService.setRoleToUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}
