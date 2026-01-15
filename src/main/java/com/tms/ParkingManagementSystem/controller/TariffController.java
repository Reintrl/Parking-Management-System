package com.tms.ParkingManagementSystem.controller;

import com.tms.ParkingManagementSystem.model.Tariff;
import com.tms.ParkingManagementSystem.model.dto.TariffCreateUpdateDto;
import com.tms.ParkingManagementSystem.service.TariffService;
import jakarta.validation.Valid;
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
import java.util.Optional;

@RestController
@RequestMapping("/tariff")
public class TariffController {
    private final TariffService tariffService;

    TariffController(TariffService tariffService) {
        this.tariffService = tariffService;
    }

    @GetMapping
    public ResponseEntity<List<Tariff>> getAllTariffs() {
        List<Tariff> tariffs = tariffService.getAllTariffs();
        if (tariffs.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tariffs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tariff> getTariffById(@PathVariable Long id) {
        Optional<Tariff> tariff = tariffService.getTariffById(id);
        if (tariff.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(tariff.get());
    }

    @PostMapping
    public ResponseEntity<Tariff> createTariff(@Valid @RequestBody TariffCreateUpdateDto tariffDto) {
        Tariff saved = tariffService.createTariff(tariffDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tariff> updateTariff(
            @PathVariable Long id,
            @Valid @RequestBody TariffCreateUpdateDto tariffDto) {

        Tariff updated = tariffService.updateTariff(id, tariffDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Tariff> deleteTariffById(@PathVariable Long id) {
        if (tariffService.deleteTariffById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
