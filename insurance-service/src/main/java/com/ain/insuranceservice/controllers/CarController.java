package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.services.CarService;
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
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @GetMapping
    public ResponseEntity<List<CarResponseDTO>> getAllCars() {
        List<CarResponseDTO> cars = carService.getCars();
        return new ResponseEntity<>(cars, HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<CarResponseDTO> createCar(@Valid @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO carResponseDTO = carService.createCar(carRequestDTO);
        return new ResponseEntity<>(carResponseDTO, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<CarResponseDTO> updateCar(@PathVariable UUID id, @Validated({Default.class}) @RequestBody CarRequestDTO carRequestDTO) {
        CarResponseDTO carResponseDTO = carService.updateCar(id, carRequestDTO);
        return new ResponseEntity<>(carResponseDTO, HttpStatus.OK);
    }
}
