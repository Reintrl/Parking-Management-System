package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.ParkingStatus;
import com.tms.ParkingManagementSystem.exception.AddressAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.ParkingLotNotFoundException;
import com.tms.ParkingManagementSystem.exception.TariffNotFoundException;
import com.tms.ParkingManagementSystem.model.ParkingLot;
import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotCreateUpdateDto;
import com.tms.ParkingManagementSystem.model.dto.ParkingLotUpdateStatusDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.TariffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final TariffRepository tariffRepository;

    ParkingLotService(ParkingLotRepository parkingLotRepository, TariffRepository tariffRepository) {
        this.parkingLotRepository = parkingLotRepository;
        this.tariffRepository = tariffRepository;
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

        ParkingLot parkingLot = new ParkingLot();
        parkingLot.setAddress(dto.getAddress());
        parkingLot.setTariff(tariff);
        parkingLot.setName(dto.getName());
        parkingLot.setCreated(LocalDateTime.now());
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
        parkingLot.setAddress(dto.getAddress());
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

    public Boolean deleteParkingLotById(Long id) {
        if (!parkingLotRepository.existsById(id)) {
            throw new ParkingLotNotFoundException(id);
        }
        parkingLotRepository.deleteById(id);
        return true;
    }
}
