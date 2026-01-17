package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.TariffCreateUpdateDto;
import com.tms.ParkingManagementSystem.service.TariffService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tariff")
public class TariffController {

    private final TariffService tariffService;

    public TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        log.info("Request: get all tariffs");
        List<Tariff> tariffs = tariffService.getAllTariffs();

        if (tariffs.isEmpty()) {
            log.warn("No tariffs found");
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} tariffs", tariffs.size());
        return ResponseEntity.ok(tariffs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tariff> getTariffById(@PathVariable Long id) {
        log.info("Request: get tariff by id = {}", id);
        Tariff tariff = tariffService.getTariffById(id);

        log.info("Tariff found id = {}", id);
        return ResponseEntity.ok(tariff);
    }

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@Valid @RequestBody TariffCreateUpdateDto tariffDto) {
        log.info("Request: create tariff");
        log.debug("Create tariff payload: {}", tariffDto);
        Tariff saved = tariffService.createTariff(tariffDto);

        log.info("Tariff created id = {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tariff> updateTariff(
            @PathVariable Long id,
            @Valid @RequestBody TariffCreateUpdateDto tariffDto) {

        log.info("Request: update tariff id = {}", id);
        log.debug("Update tariff payload: {}", tariffDto);
        Tariff updated = tariffService.updateTariff(id, tariffDto);

        log.info("Tariff updated id = {}", id);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tariff> deleteTariffById(@PathVariable Long id) {
        log.info("Request: delete tariff id = {}", id);
        boolean deleted = tariffService.deleteTariffById(id);
        if (deleted) {
            log.info("Tariff deleted id = {}", id);
            return ResponseEntity.noContent().build();
        }

        log.error("Failed to delete tariff id = {}", id);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
