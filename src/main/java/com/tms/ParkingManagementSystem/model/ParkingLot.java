package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.ParkingStatus;

import java.util.List;

public class ParkingLot {
    private Integer id;
    private String name;
    private String address;
    private ParkingStatus status;
    private List<Spot> spots;
    private Tariff tariff;
}