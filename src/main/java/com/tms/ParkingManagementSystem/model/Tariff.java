package com.tms.ParkingManagementSystem.model;

public class Tariff {

    private Integer id;
    private String name;
    private Double hourPrice;
    private Integer billingStepMinutes;
    private Integer freeMinutes;
    private boolean active = true;
}
