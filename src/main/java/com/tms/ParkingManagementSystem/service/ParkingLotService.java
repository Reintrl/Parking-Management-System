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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final TariffRepository tariffRepository;
    private final SpotRepository spotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository, TariffRepository tariffRepository, SpotRepository spotRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.tariffRepository = tariffRepository;
        this.spotRepository = spotRepository;
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot getParkingLotById(Long id) {
        return parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));
    }

    @Transactional
    public ParkingLot createParkingLot(ParkingLotCreateUpdateDto dto) {
        if (parkingLotRepository.existsParkingLotByAddress(dto.getAddress())) {
            throw new AddressAlreadyExistsException(dto.getAddress());
        }

        Tariff tariff = tariffRepository.findById(dto.getTariffId())
                .orElseThrow(() -> new TariffNotFoundException(dto.getTariffId()));

        ParkingLot parkingLot = new ParkingLot(dto.getAddress(), LocalDateTime.now());
        if (tariff.getStatus() == TariffStatus.INACTIVE) {
            tariff.setStatus(TariffStatus.ACTIVE);
            tariffRepository.save(tariff);
        }
        parkingLot.setTariff(tariff);
        parkingLot.setName(dto.getName());
        parkingLot.setChanged(LocalDateTime.now());
        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public ParkingLot updateParkingLot(Long id, ParkingLotCreateUpdateDto dto) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        Tariff newTariff = tariffRepository.findById(dto.getTariffId())
                .orElseThrow(() -> new TariffNotFoundException(dto.getTariffId()));

        Tariff oldTariff = parkingLot.getTariff();

        if (newTariff.getStatus() == TariffStatus.INACTIVE) {
            newTariff.setStatus(TariffStatus.ACTIVE);
        }

        parkingLot.setTariff(newTariff);
        parkingLot.setName(dto.getName());
        parkingLot.setChanged(LocalDateTime.now());

        ParkingLot saved = parkingLotRepository.save(parkingLot);

        if (!newTariff.getId().equals(oldTariff.getId())
                && parkingLotRepository.countByTariffId(oldTariff.getId()) == 0) {
            oldTariff.setStatus(TariffStatus.INACTIVE);
        }

        return saved;
    }


    public ParkingLot changeStatus(Long id, ParkingLotUpdateStatusDto dto) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        parkingLot.setStatus(dto.getStatus());
        parkingLot.setChanged(LocalDateTime.now());
        return parkingLotRepository.save(parkingLot);
    }

    @Transactional
    public Boolean deleteParkingLotById(Long id) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        Tariff oldTariff = parkingLot.getTariff();

        spotRepository.deleteAllByParkingLotId(id);
        parkingLotRepository.delete(parkingLot);
        if (!parkingLotRepository.existsByTariffId(oldTariff.getId())) {
            oldTariff.setStatus(TariffStatus.INACTIVE);
            tariffRepository.save(oldTariff);
        }

        return true;
    }
}
