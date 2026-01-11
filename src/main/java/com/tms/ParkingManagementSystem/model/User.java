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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "First name must not be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "Second name must not be blank")
    @Size(min = 2, max = 50, message = "Second name must be between 2 and 50 characters")
    @Column(name = "second_name", nullable = false)
    private String secondName;

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
