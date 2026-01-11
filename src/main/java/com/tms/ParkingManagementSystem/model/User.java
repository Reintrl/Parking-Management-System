package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
public class User {

    @Id
    @SequenceGenerator(
            name = "user_generator",
            sequenceName = "users_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "user_generator")
    private Long id;

    @NotNull(message = "Email must not be null")
    @Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotNull(message = "User status must not be null")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime changed;

    @NotNull(message = "Disabled permit flag must not be null")
    @Column(nullable = false)
    private Boolean disabledPermit = false;
}
