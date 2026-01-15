package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.TariffStatus;
import com.tms.ParkingManagementSystem.exception.PlateNumberAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.TariffInUseException;
import com.tms.ParkingManagementSystem.exception.TariffNameAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.TariffNotFoundException;
import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.TariffCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.TariffRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TariffService {
    private final TariffRepository tariffRepository;
    private final ParkingLotRepository parkingLotRepository;

    TariffService(TariffRepository tariffRepository,
                  ParkingLotRepository parkingLotRepository) {
        this.tariffRepository = tariffRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<Tariff> getAllTariffs() {
        return tariffRepository.findAll();
    }

    public Optional<Tariff> getTariffById(Long id) {
        return tariffRepository.findById(id);
    }

    public Tariff createTariff(TariffCreateUpdateDto tariffDto) {
        if (tariffRepository.existsByName(tariffDto.getName())) {
            throw new TariffNameAlreadyExistsException(tariffDto.getName());
        }
        Tariff tariff = new Tariff(LocalDateTime.now());
        tariff.setName(tariffDto.getName());
        tariff.setHourPrice(tariffDto.getHourPrice());
        tariff.setFreeMinutes(tariffDto.getFreeMinutes());
        tariff.setBillingStepMinutes(tariffDto.getBillingStepMinutes());
        tariff.setChanged(LocalDateTime.now());
        tariff.setStatus(TariffStatus.INACTIVE);
        return tariffRepository.save(tariff);
    }

    public Tariff changeStatus(Long id, TariffStatus status) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new TariffNotFoundException(id));

        tariff.setStatus(status);
        tariff.setChanged(LocalDateTime.now());
        return tariffRepository.save(tariff);
    }

    public Tariff updateTariff(Long id, TariffCreateUpdateDto tariffDto) {
        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new TariffNotFoundException(id));

        if (!tariffDto.getName().equals(tariff.getName())
                && tariffRepository.existsByName(tariffDto.getName())) {
            throw new PlateNumberAlreadyExistsException(tariffDto.getName());
        }

        tariff.setName(tariffDto.getName());
        tariff.setHourPrice(tariffDto.getHourPrice());
        tariff.setFreeMinutes(tariffDto.getFreeMinutes());
        tariff.setBillingStepMinutes(tariffDto.getBillingStepMinutes());
        tariff.setChanged(LocalDateTime.now());
        return tariffRepository.save(tariff);
    }

    public boolean deleteTariffById(Long id) {
        if (!tariffRepository.existsById(id)) {
            throw new TariffNotFoundException(id);
        }

        if (parkingLotRepository.existsByTariffId(id)) {
            throw new TariffInUseException(id);
        }

        tariffRepository.deleteById(id);
        return true;
    }
}
