package com.ain.insuranceservice.mappers;


import com.ain.insuranceservice.dto.DriverRequestDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DriverMapper {
    public static DriverResponseDTO toDTO(Driver driver) {
        DriverResponseDTO driverDTO = new DriverResponseDTO();
        driverDTO.setId(driver.getId().toString());
        driverDTO.setPolicyNumber(driver.getPolicy() != null ? driver.getPolicy().getPolicyNumber() : null);
        driverDTO.setFullName(driver.getFullName());
        driverDTO.setBirthDate(driver.getBirthDate().toString());
        driverDTO.setLicenseNumber(driver.getLicenseNumber());
        driverDTO.setDrivingExperience(driver.getDrivingExperience().toString());

        return driverDTO;
    }

    public static Driver toModel(DriverRequestDTO driverRequestDTO) {
        Driver driver = new Driver();
//        driver.setPolicy(driverRequestDTO.getPolicy());
        driver.setFullName(driverRequestDTO.getFullName());
        driver.setBirthDate(LocalDate.parse(driverRequestDTO.getBirthDate()));
        driver.setLicenseNumber(driverRequestDTO.getLicenseNumber());
        driver.setDrivingExperience(LocalDate.parse(driverRequestDTO.getDrivingExperience()));
        return driver;
    }
}



