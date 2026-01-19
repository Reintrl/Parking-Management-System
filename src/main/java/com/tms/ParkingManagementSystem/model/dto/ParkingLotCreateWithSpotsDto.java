package com.tms.ParkingManagementSystem.model.dto;

import com.tms.ParkingManagementSystem.enums.SpotType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ParkingLotCreateWithSpotsDto {

    @Valid
    @NotNull(message = "Parking lot payload must not be null")
    private ParkingLotCreateUpdateDto parkingLot;

    @NotNull(message = "Spots payload must not be null")
    @Valid
    private BulkSpotsCreateDto spots;

    @Data
    public static class BulkSpotsCreateDto {

        @Min(value = 1, message = "Count must be >= 1")
        private int count;

        @Min(value = 1, message = "Start number must be >= 1")
        private int startNumber = 1;

        @NotNull(message = "Levels must be specified")
        private List<@NotNull Integer> levels;

        @NotNull(message = "Types must be specified")
        private List<@NotNull SpotType> types;
    }
}
