package com.tms.ParkingManagementSystem.security;

import com.tms.ParkingManagementSystem.exception.ReservationAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.SessionAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.VehicleAccessDeniedException;
import com.tms.ParkingManagementSystem.exception.VehicleNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingSession;
import com.tms.ParkingManagementSystem.model.Reservation;
import com.tms.ParkingManagementSystem.model.User;
import com.tms.ParkingManagementSystem.model.Vehicle;
import com.tms.ParkingManagementSystem.repository.VehicleRepository;
import com.tms.ParkingManagementSystem.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private final UserService userService;
    private final VehicleRepository vehicleRepository;

    public SecurityUtil(UserService userService, VehicleRepository vehicleRepository) {
        this.userService = userService;
        this.vehicleRepository = vehicleRepository;
    }

    public boolean isAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null
                && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    public Vehicle assertVehicleOwnerOrAdmin(Long vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        if (isAdmin()) {
            return vehicle;
        }

        User currentUser = userService.getCurrentUser();
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
        User currentUser = userService.getCurrentUser();
        Long ownerId = session.getVehicle().getUser().getId();
        if (!ownerId.equals(currentUser.getId())) {
            throw new SessionAccessDeniedException(session.getId());
        }
    }

    public void assertReservationOwnerOrAdmin(Reservation reservation) {
        if (isAdmin()) {
            return;
        }
        User currentUser = userService.getCurrentUser();
        Long ownerId = reservation.getVehicle().getUser().getId();
        if (!ownerId.equals(currentUser.getId())) {
            throw new ReservationAccessDeniedException(reservation.getId());
        }
    }
}