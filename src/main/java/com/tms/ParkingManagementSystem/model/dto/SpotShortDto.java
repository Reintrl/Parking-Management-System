package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SpotType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SpotShortDto {

    private Long id;
    private Integer number;
    private Integer level;
    private SpotType type;

    private Long parkingLotId;
    private String parkingLotName;
}
