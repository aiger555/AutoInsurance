package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.dto.DriverRequestDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.DriverService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Data
@RestController
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    public ResponseEntity<List<DriverResponseDTO>> getDrivers() {
        List<DriverResponseDTO> drivers = driverService.getDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driverResponseDTO = driverService.createDriver(driverRequestDTO);
        return new ResponseEntity<>(driverResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDTO> updateDriver(@PathVariable UUID id, @Validated({Default.class}) @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driverResponseDTO = driverService.updateDriver(id, driverRequestDTO);
        return new ResponseEntity<>(driverResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

