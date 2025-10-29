package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.mappers.CarMapper;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.repositories.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    private CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public List<CarResponseDTO> getCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream()
                .map(CarMapper::toDTO).toList();
    }

    public CarResponseDTO createCar(CarRequestDTO carRequestDTO) {
        Car newCar = carRepository.save(CarMapper.toModel(carRequestDTO));
        return CarMapper.toDTO(newCar);

    }
}
