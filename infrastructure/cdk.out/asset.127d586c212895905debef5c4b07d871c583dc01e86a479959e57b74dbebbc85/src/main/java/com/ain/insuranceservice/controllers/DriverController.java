package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.dto.DriverRequestDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/drivers")
@Tag(name = "Driver", description = "API for managing Drivers") // swagger
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Get Drivers") // swagger
    public ResponseEntity<List<DriverResponseDTO>> getDrivers() {
        List<DriverResponseDTO> drivers = driverService.getDrivers();
        return new ResponseEntity<>(drivers, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new Driver") // swagger
    public ResponseEntity<DriverResponseDTO> createDriver(@Valid @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driverResponseDTO = driverService.createDriver(driverRequestDTO);
        return new ResponseEntity<>(driverResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Driver") // swagger
    public ResponseEntity<DriverResponseDTO> updateDriver(@PathVariable UUID id, @Validated({Default.class}) @RequestBody DriverRequestDTO driverRequestDTO) {
        DriverResponseDTO driverResponseDTO = driverService.updateDriver(id, driverRequestDTO);
        return new ResponseEntity<>(driverResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Driver") // swagger
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

