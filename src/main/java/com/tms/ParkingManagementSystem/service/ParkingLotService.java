package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ParkingStatus;
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

    ParkingLotService(ParkingLotRepository parkingLotRepository, TariffRepository tariffRepository, SpotRepository spotRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.tariffRepository = tariffRepository;
        this.spotRepository = spotRepository;
    }

    public List<ParkingLot> getAllParkingLots() {
        return parkingLotRepository.findAll();
    }

    public Optional<ParkingLot> getParkingLotById(Long id) {
        return parkingLotRepository.findById(id);
    }

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

    public ParkingLot updateParkingLot(Long id, ParkingLotCreateUpdateDto dto) {
        ParkingLot parkingLot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new ParkingLotNotFoundException(id));

        if (!dto.getAddress().equals(parkingLot.getAddress()) && parkingLotRepository.existsParkingLotByAddress(dto.getAddress())) {
            throw new AddressAlreadyExistsException(dto.getAddress());
        }

        Optional<Tariff> tariffFromUpdate = tariffRepository.findById(dto.getTariffId());
        if (tariffFromUpdate.isEmpty()) {
            throw new TariffNotFoundException(dto.getTariffId());
        }

        parkingLot.setTariff(tariffFromUpdate.get());
        parkingLot.setName(dto.getName());
        parkingLot.setChanged(LocalDateTime.now());

        return parkingLotRepository.save(parkingLot);
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
        if (!parkingLotRepository.existsById(id)) {
            throw new ParkingLotNotFoundException(id);
        }

        spotRepository.deleteAllByParkingLotId(id);
        parkingLotRepository.deleteById(id);
        return true;
    }
}
