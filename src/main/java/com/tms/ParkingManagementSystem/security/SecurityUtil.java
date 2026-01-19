package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.exception.ReservationAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.SessionAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.UserNotFoundException;
import com.tms.ParkingManagementSystem.exception.VehicleAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.Security;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.repository.UserRepository;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final VehicleRepository vehicleRepository;
    private final SecurityRepository securityRepository;
    private final UserRepository userRepository;

    public SecurityUtil(VehicleRepository vehicleRepository,
                        SecurityRepository securityRepository,
                        UserRepository userRepository) {
        this.vehicleRepository = vehicleRepository;
        this.securityRepository = securityRepository;
        this.userRepository = userRepository;
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"));
    }

    public Vehicle assertVehicleOwnerOrAdmin(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if (isAdmin()) {
            return vehicle;
        }

        User currentUser = getCurrentUser();
        Long ownerId = vehicle.getUser().getId();

        if (!ownerId.equals(currentUser.getId())) {
            throw new VehicleAccessDeniedException(vehicleId);
        }

        return vehicle;
    }

    public void assertSessionOwnerOrAdmin(ParkingSession session) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        Long ownerId = session.getVehicle().getUser().getId();
        if (!ownerId.equals(currentUser.getId())) {
            throw new SessionAccessDeniedException(session.getId());
        }
    }

    public void assertReservationOwnerOrAdmin(Reservation reservation) {
        if (isAdmin()) {
            return;
        }
        User currentUser = getCurrentUser();
        Long ownerId = reservation.getVehicle().getUser().getId();
        if (!ownerId.equals(currentUser.getId())) {
            throw new ReservationAccessDeniedException(reservation.getId());
        }
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new UserNotFoundException("No authentication in security context");
        }

        String username = auth.getName();

        Security sec = securityRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found by username: " + username));

        return sec.getUser();
    }
}