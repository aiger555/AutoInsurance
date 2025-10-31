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
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {
    private final InsurancePolicyRepository insurancePolicyRepository;
    private DriverReposiroty driverReposiroty;

    public DriverService(DriverReposiroty driverReposiroty, InsurancePolicyRepository insurancePolicyRepository) {
        this.driverReposiroty = driverReposiroty;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    public List<DriverResponseDTO> getDrivers() {
        List<Driver> drivers = driverReposiroty.findAll();
        return drivers.stream()
                .map(DriverMapper::toDTO).toList();
    }
    public DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO) {
        InsurancePolicy policy =  insurancePolicyRepository.findById(driverRequestDTO.getPolicy().getPolicyNumber())
                .orElseThrow(() -> new RuntimeException("Policy not found with number: " + driverRequestDTO.getPolicy().getPolicyNumber()));
        Driver newDriver = driverReposiroty.save(DriverMapper.toModel(driverRequestDTO));
        newDriver.setPolicy(policy);
        Driver savedDriver = driverReposiroty.save(newDriver);
        return DriverMapper.toDTO(savedDriver);
    }
}
