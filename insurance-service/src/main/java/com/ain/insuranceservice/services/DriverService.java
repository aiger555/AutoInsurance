package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.mappers.CarMapper;
import com.ain.insuranceservice.mappers.DriverMapper;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.repositories.DriverReposiroty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private DriverReposiroty driverReposiroty;

    public DriverService(DriverReposiroty driverReposiroty) {
        this.driverReposiroty = driverReposiroty;
    }

    public List<DriverResponseDTO> getCars() {
        List<Driver> drivers = driverReposiroty.findAll();
        return drivers.stream()
                .map(DriverMapper::toDTO).toList();
    }
}
