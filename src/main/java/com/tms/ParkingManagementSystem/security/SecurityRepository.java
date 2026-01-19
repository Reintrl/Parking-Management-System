    package com.tms.ParkingManagementSystem.security;

    import com.tms.ParkingManagementSystem.model.Security;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Repository;

    import java.util.Optional;

    @Repository
    public interface SecurityRepository extends JpaRepository<Security, Long> {
        Optional<Security> findByUsername(String username);
        boolean existsByUsername(String username);
    }
