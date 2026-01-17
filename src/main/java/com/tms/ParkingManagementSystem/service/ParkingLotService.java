package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.TariffStatus;
import com.tms.ParkingManagementSystem.exception.AddressAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.ParkingLotNotFoundException;
import com.tms.ParkingManagementSystem.exception.TariffNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotUpdateStatusDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.SpotRepository;
import com.tms.ParkingManagementSystem.repository.TariffRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final TariffRepository tariffRepository;
    private final SpotRepository spotRepository;

    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            TariffRepository tariffRepository,
            SpotRepository spotRepository) {

        this.parkingLotRepository = parkingLotRepository;
        this.tariffRepository = tariffRepository;
        this.spotRepository = spotRepository;
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
}
