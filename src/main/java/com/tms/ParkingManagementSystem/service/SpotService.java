package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.exception.ParkingLotNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNotFoundException;
import com.tms.ParkingManagementSystem.exception.SpotNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.dto.SpotCreateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotStatusUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.SpotUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SpotService {

    private final SpotRepository spotRepository;
    private final ParkingLotRepository parkingLotRepository;

    public SpotService(SpotRepository spotRepository,
                       ParkingLotRepository parkingLotRepository) {
        this.spotRepository = spotRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<Spot> getAllSpots() {
        return spotRepository.findAll();
    }

    public Optional<Spot> getSpotById(Long id) {
        return spotRepository.findById(id);
    }

    public Spot createSpot(SpotCreateDto dto) {
        ParkingLot parkingLot = parkingLotRepository.findById(dto.getParkingLotId())
                .orElseThrow(() -> new ParkingLotNotFoundException(dto.getParkingLotId()));

        if (spotRepository.existsByParkingLotIdAndNumber(parkingLot.getId(), dto.getNumber())) {
            throw new SpotNumberAlreadyExistsException(parkingLot.getId(), dto.getNumber());
        }

        Spot spot = new Spot(dto.getNumber(), parkingLot, dto.getLevel(), LocalDateTime.now());
        spot.setType(dto.getType());
        spot.setChanged(LocalDateTime.now());

        return spotRepository.save(spot);
    }


    public Spot updateSpot(Long id, SpotUpdateDto dto) {
        Spot spotForUpdate = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spotForUpdate.setType(dto.getType());
        spotForUpdate.setChanged(LocalDateTime.now());

        return spotRepository.save(spotForUpdate);
    }


    public Spot changeStatus(Long id, SpotStatusUpdateDto dto) {
        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spot.setStatus(dto.getStatus());
        spot.setChanged(LocalDateTime.now());
        return spotRepository.save(spot);
    }

    public boolean deleteSpotById(Long id) {
        if (!spotRepository.existsById(id)) {
            throw new SpotNotFoundException(id);
        }
        spotRepository.deleteById(id);
        return true;
    }

    public List<Spot> getSpotsByParkingLotId(Long parkingLotId) {
        if (!parkingLotRepository.existsById(parkingLotId)) {
            throw new ParkingLotNotFoundException(parkingLotId);
        }
        return spotRepository.findByParkingLotId(parkingLotId);
    }
}
