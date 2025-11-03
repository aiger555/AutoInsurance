package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.services.CarService;
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
@RequestMapping("/cars")
@Tag(name = "Car", description = "API for managing Cars") // swagger
public class CarController {
    private final CarService carService;

    @GetMapping
    @Operation(summary = "Get Cars") // swagger
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<CarResponseDTO> cars = carService.getCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new Car") // swagger
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO carResponseDTO = carService.createCar(carRequestDTO);
        return new ResponseEntity<>(carResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a Car") // swagger
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable UUID id, @Validated({Default.class}) @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO carResponseDTO = carService.updateCar(id, carRequestDTO);
        return new ResponseEntity<>(carResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Car") // swagger
    public ResponseEntity<Void> deleteCar(@PathVariable UUID id) {
        carService.deleteCar(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
