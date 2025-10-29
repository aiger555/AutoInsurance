package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.services.CarService;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
