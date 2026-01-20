    package com.tms.ParkingManagementSystem.security;

    import com.tms.ParkingManagementSystem.model.Security;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Modifying;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.stereotype.Repository;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface SecurityRepository extends JpaRepository<Security, Long> {
        Optional<Security> findByUsername(String username);
        boolean existsByUsername(String username);
        @Query(nativeQuery = true, value = "SELECT * FROM security WHERE role = :roleParam")
        List<Security> customFindByRole(String roleParam);

        @Transactional
        @Modifying
        @Query(nativeQuery = true, value = "UPDATE security SET role = 'ADMIN' WHERE user_id = :userId")
        int setAdminRoleByUserId(Long userId);

        @Transactional
        @Modifying
        @Query(nativeQuery = true, value = "UPDATE security SET role = 'OPERATOR' WHERE user_id = :userId")
        int setOperatorRoleByUserId(Long userId);

        @Transactional
        @Modifying
        @Query(nativeQuery = true, value = "UPDATE security SET role = 'USER' WHERE user_id = :userId")
        int setUserRoleByUserId(Long userId);
    }
