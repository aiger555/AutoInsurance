package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.exception.LicenseNumberAlreadyExistsException;
import com.ain.insuranceservice.mappers.DriverMapper;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.DriverRepository;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import org.springframework.stereotype.Service;
import com.ain.insuranceservice.exception.DriverNotFoundException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class DriverService {
    private final InsurancePolicyRepository insurancePolicyRepository;
    private DriverRepository driverReposiroty;

    public DriverService(DriverRepository driverReposiroty, InsurancePolicyRepository insurancePolicyRepository) {
        this.driverReposiroty = driverReposiroty;
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    public List<DriverResponseDTO> getDrivers() {
        List<Driver> drivers = driverReposiroty.findAll();
        return drivers.stream()
                .map(DriverMapper::toDTO).toList();
    }
    public DriverResponseDTO createDriver(DriverRequestDTO driverRequestDTO) {
        if (driverReposiroty.existsByLicenseNumber(driverRequestDTO.getLicenseNumber())) {
            throw new LicenseNumberAlreadyExistsException("A driver with this license number already exists" + driverRequestDTO.getLicenseNumber());
        }

        InsurancePolicy policy =  insurancePolicyRepository.findById(driverRequestDTO.getPolicy().getPolicyNumber())
                .orElseThrow(() -> new RuntimeException("Policy not found with number: " + driverRequestDTO.getPolicy().getPolicyNumber()));
        Driver newDriver = driverReposiroty.save(DriverMapper.toModel(driverRequestDTO));
        newDriver.setPolicy(policy);
        Driver savedDriver = driverReposiroty.save(newDriver);
        return DriverMapper.toDTO(savedDriver);
    }

    public DriverResponseDTO updateDriver(UUID id, DriverRequestDTO driverRequestDTO) {
        Driver driver = driverReposiroty.findById(id).orElseThrow(
                () -> new DriverNotFoundException("Driver not found with ID: " + id));

        if (driverReposiroty.existsByLicenseNumber(driverRequestDTO.getLicenseNumber())) {
            throw new LicenseNumberAlreadyExistsException("A driver with this license number already exists" + driverRequestDTO.getLicenseNumber());
        }

        driver.setPolicy(driverRequestDTO.getPolicy());
        driver.setFullName(driverRequestDTO.getFullName());
        driver.setBirthDate(LocalDate.parse(driverRequestDTO.getBirthDate()));
        driver.setLicenseNumber(driverRequestDTO.getLicenseNumber());
        driver.setDrivingExperience(LocalDate.parse(driverRequestDTO.getDrivingExperience()));

        Driver updatedDriver = driverReposiroty.save(driver);
        return DriverMapper.toDTO(updatedDriver);

    }
}
