package com.ain.insuranceservice.mappers;


import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.models.Driver;

public class DriverMapper {
    public static DriverResponseDTO toDTO(Driver driver) {
        DriverResponseDTO driverDTO = new DriverResponseDTO();
        driverDTO.setId(driver.getId().toString());
        driverDTO.setFullName(driver.getFullName());
        driverDTO.setBirthDate(driver.getBirthDate().toString());
        driverDTO.setLicenseNumber(driver.getLicenseNumber());
        driverDTO.setDrivingExperience(driver.getDrivingExperience().toString());

        return driverDTO;
    }
}
