package com.tms.ParkingManagementSystem.service;

import com.tms.ParkingManagementSystem.enums.TariffStatus;
import com.tms.ParkingManagementSystem.exception.TariffInUseException;
import com.tms.ParkingManagementSystem.exception.TariffNameAlreadyExistsException;
import com.tms.ParkingManagementSystem.exception.TariffNotFoundException;
import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.TariffCreateUpdateDto;
import com.tms.ParkingManagementSystem.repository.ParkingLotRepository;
import com.tms.ParkingManagementSystem.repository.TariffRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class TariffService {

    private final TariffRepository tariffRepository;
    private final ParkingLotRepository parkingLotRepository;

    public TariffService(TariffRepository tariffRepository,
                         ParkingLotRepository parkingLotRepository) {
        this.tariffRepository = tariffRepository;
        this.parkingLotRepository = parkingLotRepository;
    }

    public List<Tariff> getAllTariffs() {
        log.info("Get all tariffs");

        List<Tariff> tariffs = tariffRepository.findAll();
        log.info("Found {} tariffs", tariffs.size());

        return tariffs;
    }

    public Tariff getTariffById(Long id) {
        log.info("Get tariff by id = {}", id);

        return tariffRepository.findById(id)
                .orElseThrow(() -> new TariffNotFoundException(id));
    }

    public Tariff createTariff(TariffCreateUpdateDto tariffDto) {
        log.info("Create tariff");
        log.debug("Create tariff payload = {}", tariffDto);

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

        Tariff saved = tariffRepository.save(tariff);

        log.info("Tariff created, id = {}, name = {}", saved.getId(), saved.getName());
        return saved;
    }

    public Tariff updateTariff(Long id, TariffCreateUpdateDto tariffDto) {
        log.info("Update tariff, id = {}", id);
        log.debug("Update tariff payload = {}", tariffDto);

        Tariff tariff = tariffRepository.findById(id)
                .orElseThrow(() -> new TariffNotFoundException(id));

        if (!tariffDto.getName().equals(tariff.getName())
                && tariffRepository.existsByName(tariffDto.getName())) {
            throw new TariffNameAlreadyExistsException(tariffDto.getName());
        }

        tariff.setName(tariffDto.getName());
        tariff.setHourPrice(tariffDto.getHourPrice());
        tariff.setFreeMinutes(tariffDto.getFreeMinutes());
        tariff.setBillingStepMinutes(tariffDto.getBillingStepMinutes());
        tariff.setChanged(LocalDateTime.now());

        Tariff saved = tariffRepository.save(tariff);

        log.info("Tariff updated, id = {}, name = {}", saved.getId(), saved.getName());
        return saved;
    }

    public boolean deleteTariffById(Long id) {
        log.info("Delete tariff, id = {}", id);

        if (!tariffRepository.existsById(id)) {
            throw new TariffNotFoundException(id);
        }

        if (parkingLotRepository.existsByTariffId(id)) {
            throw new TariffInUseException(id);
        }

        tariffRepository.deleteById(id);

        log.info("Tariff deleted, id = {}", id);
        return true;
    }
}
