package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ReservationStatus;
import com.tms.ParkingManagementSystem.enums.SessionStatus;
import com.tms.ParkingManagementSystem.enums.SpotType;
import com.tms.ParkingManagementSystem.enums.TariffStatus;
import com.tms.ParkingManagementSystem.exception.AddressAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.ParkingLotInUseException;
import com.tms.ParkingManagementSystem.exception.ParkingLotNotFoundException;
import com.tms.ParkingManagementSystem.exception.TariffNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.Spot;
import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateWithSpotsDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotUpdateStatusDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.ParkingSessionRepository;
import com.tms.ParkingManagementSystem.repository.ReservationRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import com.tms.ParkingManagementSystem.repository.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final TariffRepository tariffRepository;
    private final SpotRepository spotRepository;
    private final ParkingSessionRepository parkingSessionRepository;
    private final ReservationRepository reservationRepository;

    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            TariffRepository tariffRepository,
            SpotRepository spotRepository,
            ParkingSessionRepository parkingSessionRepository,
            ReservationRepository reservationRepository) {

        this.parkingLotRepository = parkingLotRepository;
        this.tariffRepository = tariffRepository;
        this.spotRepository = spotRepository;
        this.parkingSessionRepository = parkingSessionRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<ParkingLot> getAllParkingLots() {
        log.info("Get all parking lots");
        return parkingLotRepository.findAll();
    }

    public ParkingLot getParkingLotById(Long id) {
        log.info("Get parking lot by id = {}", id);
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));
    }

    @Transactional
    public ParkingLot createParkingLot(ParkingLotCreateUpdateDto dto) {
        log.info("Create parking lot");
        log.debug("Create parking lot payload = {}", dto);

        if (parkingLotRepository.existsParkingLotByAddress(dto.getAddress())) {
            throw new AddressAlreadyExistsException(dto.getAddress());
        }

        Tariff tariff = tariffRepository.findById(dto.getTariffId())
                .orElseThrow(() -> new TariffNotFoundException(dto.getTariffId()));

        if (tariff.getStatus() == TariffStatus.INACTIVE) {
            tariff.setStatus(TariffStatus.ACTIVE);
            tariffRepository.save(tariff);
            log.info("Tariff activated, tariffId = {}", tariff.getId());
        }

        ParkingLot parkingLot = new ParkingLot(dto.getAddress(), LocalDateTime.now());
        parkingLot.setTariff(tariff);
        parkingLot.setName(dto.getName());
        parkingLot.setChanged(LocalDateTime.now());

        ParkingLot saved = parkingLotRepository.save(parkingLot);

        log.info("Parking lot created, id = {}", saved.getId());
        return saved;
    }

    @Transactional
    public ParkingLot updateParkingLot(Long id, ParkingLotCreateUpdateDto dto) {
        log.info("Update parking lot, id = {}", id);
        log.debug("Update parking lot payload = {}", dto);

        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        Tariff newTariff = tariffRepository.findById(dto.getTariffId())
                .orElseThrow(() -> new TariffNotFoundException(dto.getTariffId()));

        Tariff oldTariff = parkingLot.getTariff();

        if (newTariff.getStatus() == TariffStatus.INACTIVE) {
            newTariff.setStatus(TariffStatus.ACTIVE);
            log.info("Tariff activated, tariffId = {}", newTariff.getId());
        }

        parkingLot.setTariff(newTariff);
        parkingLot.setName(dto.getName());
        parkingLot.setChanged(LocalDateTime.now());

        ParkingLot saved = parkingLotRepository.save(parkingLot);

        if (!newTariff.getId().equals(oldTariff.getId())
                && parkingLotRepository.countByTariffId(oldTariff.getId()) == 0) {
            oldTariff.setStatus(TariffStatus.INACTIVE);
            log.info("Old tariff deactivated, tariffId = {}", oldTariff.getId());
        }

        log.info("Parking lot updated, id = {}", saved.getId());
        return saved;
    }

    @Transactional
    public ParkingLot changeStatus(Long id, ParkingLotUpdateStatusDto dto) {
        log.info("Change parking lot status, id = {}, status = {}", id, dto.getStatus());

        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        parkingLot.setStatus(dto.getStatus());
        parkingLot.setChanged(LocalDateTime.now());

        ParkingLot saved = parkingLotRepository.save(parkingLot);

        log.info("Parking lot status changed, id = {}", id);
        return saved;
    }

    @Transactional
    public Boolean deleteParkingLotById(Long id) {
        log.info("Delete parking lot, id = {}", id);

        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        List<Spot> spots = spotRepository.findByParkingLotId(id);
        LocalDateTime now = LocalDateTime.now();

        for (Spot spot : spots) {
            Long spotId = spot.getId();

            if (parkingSessionRepository.existsBySpotIdAndStatus(spotId, SessionStatus.ACTIVE)) {
                throw new ParkingLotInUseException(id,
                        "Spot id = " + spotId + " has an active parking session");
            }

            if (reservationRepository.existsBySpotIdAndStatusAndEndTimeAfter(spotId, ReservationStatus.ACTIVE, now)) {
                throw new ParkingLotInUseException(id,
                        "Spot id = " + spotId + " has an active (ongoing or future) reservation");
            }
        }

        Tariff oldTariff = parkingLot.getTariff();

        spotRepository.deleteAllByParkingLotId(id);
        parkingLotRepository.delete(parkingLot);

        if (!parkingLotRepository.existsByTariffId(oldTariff.getId())) {
            oldTariff.setStatus(TariffStatus.INACTIVE);
            tariffRepository.save(oldTariff);
            log.info("Tariff deactivated, tariffId = {}", oldTariff.getId());
        }

        log.info("Parking lot deleted, id = {}", id);
        return true;
    }

    @Transactional
    public ParkingLot createParkingLotWithSpots(ParkingLotCreateWithSpotsDto dto) {
        log.info("Create parking lot with bulk spots");
        log.debug("Create parking lot with spots payload = {}", dto);

        ParkingLotCreateUpdateDto lotDto = dto.getParkingLot();
        ParkingLot createdLot = createParkingLot(lotDto);

        ParkingLotCreateWithSpotsDto.BulkSpotsCreateDto s = dto.getSpots();

        if (s.getLevels().isEmpty()) {
            throw new IllegalArgumentException("Levels must not be empty");
        }
        if (s.getTypes().isEmpty()) {
            throw new IllegalArgumentException("Types must not be empty");
        }

        int count = s.getCount();
        int startNumber = s.getStartNumber();

        LocalDateTime now = LocalDateTime.now();

        List<Spot> spots = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            int number = startNumber + i;
            Integer level = s.getLevels().get(i % s.getLevels().size());
            SpotType type = s.getTypes().get(i % s.getTypes().size());

            if (spotRepository.existsByParkingLotIdAndLevelAndNumber(createdLot.getId(), level, number)) {
                log.warn("Bulk create denied: spot already exists, parkingLotId = {}, level = {}, number = {}",
                        createdLot.getId(), level, number);
                throw new IllegalArgumentException(
                        "Spot already exists: parkingLotId=" + createdLot.getId() + ", level=" + level + ", number=" + number
                );
            }

            Spot spot = new Spot(number, createdLot, level, LocalDateTime.now());
            spot.setType(type);
            spot.setChanged(now);

            spots.add(spot);
        }

        spotRepository.saveAll(spots);

        log.info("Parking lot created with spots, parkingLotId = {}, spotsCreated = {}",
                createdLot.getId(), spots.size());

        return createdLot;
    }

}
