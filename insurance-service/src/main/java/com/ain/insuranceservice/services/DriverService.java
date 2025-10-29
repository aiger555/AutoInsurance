package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.mappers.CarMapper;
import com.ain.insuranceservice.mappers.DriverMapper;
import com.ain.insuranceservice.mappers.InsurancePolicyMapper;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;
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
    public DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO) {
        Driver newDriver = driverReposiroty.save(DriverMapper.toModel(driverRequestDTO));
        return DriverMapper.toDTO(newDriver);
    }
}
