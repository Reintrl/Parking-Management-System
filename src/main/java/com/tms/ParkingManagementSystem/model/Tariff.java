package com.tms.ParkingManagementSystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@ToString
public class Tariff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private BigDecimal hourPrice;

    @Column(nullable = false)
    private Integer billingStepMinutes;

    @Column(nullable = false)
    private Integer freeMinutes = 0;

    @Column(nullable = false)
    private boolean active = true;
}
