package com.tms.ParkingManagementSystem.model;

import com.tms.ParkingManagementSystem.enums.SpotStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;

public class Spot {

    private Integer id;
    private Integer number;
    private SpotType type;
    private SpotStatus status;
    private Integer ParkingLotId;
    private Integer level;
}
