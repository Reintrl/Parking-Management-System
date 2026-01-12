package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VehicleResponseDto {
    private Long id;
    private String plateNumber;
    private VehicleType type;
    private Long userId;
}
