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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
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
        log.info("Get all spots");
        return spotRepository.findAll();
    }

    public Spot getSpotById(Long id) {
        log.info("Get spot by id = {}", id);

        return spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));
    }

    public Spot createSpot(SpotCreateDto dto) {
        log.info("Create spot");
        log.debug("Create spot payload = {}", dto);

        ParkingLot parkingLot = parkingLotRepository.findById(dto.getParkingLotId())
                .orElseThrow(() -> new ParkingLotNotFoundException(dto.getParkingLotId()));

        if (spotRepository.existsByParkingLotIdAndNumber(parkingLot.getId(), dto.getNumber())) {
            throw new SpotNumberAlreadyExistsException(parkingLot.getId(), dto.getNumber());
        }

        Spot spot = new Spot(dto.getNumber(), parkingLot, dto.getLevel(), LocalDateTime.now());
        spot.setType(dto.getType());
        spot.setChanged(LocalDateTime.now());

        Spot saved = spotRepository.save(spot);

        log.info("Spot created, id = {}, parkingLotId = {}, number = {}",
                saved.getId(), parkingLot.getId(), saved.getNumber());

        return saved;
    }

    public Spot updateSpot(Long id, SpotUpdateDto dto) {
        log.info("Update spot, id = {}", id);
        log.debug("Update spot payload = {}", dto);

        Spot spotForUpdate = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spotForUpdate.setType(dto.getType());
        spotForUpdate.setChanged(LocalDateTime.now());

        Spot saved = spotRepository.save(spotForUpdate);

        log.info("Spot updated, id = {}", saved.getId());
        return saved;
    }

    public Spot changeStatus(Long id, SpotStatusUpdateDto dto) {
        log.info("Change spot status, id = {}, status = {}", id, dto.getStatus());
        log.debug("Change spot status payload = {}", dto);

        Spot spot = spotRepository.findById(id)
                .orElseThrow(() -> new SpotNotFoundException(id));

        spot.setStatus(dto.getStatus());
        spot.setChanged(LocalDateTime.now());

        Spot saved = spotRepository.save(spot);

        log.info("Spot status changed, id = {}", saved.getId());
        return saved;
    }

    public boolean deleteSpotById(Long id) {
        log.info("Delete spot, id = {}", id);

        if (!spotRepository.existsById(id)) {
            throw new SpotNotFoundException(id);
        }

        spotRepository.deleteById(id);

        log.info("Spot deleted, id = {}", id);
        return true;
    }

    public List<Spot> getSpotsByParkingLotId(Long parkingLotId) {
        log.info("Get spots by parkingLotId = {}", parkingLotId);

        if (!parkingLotRepository.existsById(parkingLotId)) {
            throw new ParkingLotNotFoundException(parkingLotId);
        }

        List<Spot> spots = spotRepository.findByParkingLotId(parkingLotId);

        log.info("Found {} spots for parkingLotId = {}", spots.size(), parkingLotId);
        return spots;
    }
}
