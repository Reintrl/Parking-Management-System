package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.TariffStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tariffs")
@Data
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Tariff {

    @Id
    @SequenceGenerator(
            name = "tariff_generator",
            sequenceName = "tariffs_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(generator = "tariff_generator")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal hourPrice;

    @Column(nullable = false)
    private Integer billingStepMinutes;

    @Column(nullable = false)
    private Integer freeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TariffStatus status = TariffStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private final LocalDateTime created;

    @Column(nullable = false)
    private LocalDateTime changed;
}
