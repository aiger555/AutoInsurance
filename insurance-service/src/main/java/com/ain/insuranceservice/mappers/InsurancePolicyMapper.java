package com.ain.insuranceservice.mappers;


import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class InsurancePolicyMapper {
    private final ClientMapper clientMapper;
    private final CarMapper carMapper;
    private final DriverMapper driverMapper;

    public static InsurancePolicyResponseDTO toDTO(InsurancePolicy insurancePolicy) {
        InsurancePolicyResponseDTO insurancePolicyDTO = new InsurancePolicyResponseDTO();
        insurancePolicyDTO.setPolicyNumber(insurancePolicy.getPolicyNumber());
        insurancePolicyDTO.setPolicyType(insurancePolicy.getPolicyType());
        insurancePolicyDTO.setPolicyHolder(insurancePolicy.getPolicyHolder());
        insurancePolicyDTO.setPremium(insurancePolicy.getPremium().toString());
        insurancePolicyDTO.setStartDate(insurancePolicy.getStartDate().toString());
        insurancePolicyDTO.setEndDate(insurancePolicy.getEndDate().toString());
        insurancePolicyDTO.setStatus(insurancePolicy.getStatus());
        insurancePolicyDTO.setVehicleOwner(ClientMapper.toDTO(insurancePolicy.getVehicleOwner()));
        insurancePolicyDTO.setInsuredCar(CarMapper.toDTO(insurancePolicy.getInsuredCar()));
        insurancePolicyDTO.setDrivers(insurancePolicy.getDrivers().stream()
                .map(DriverMapper::toDTO).collect(Collectors.toList()));
        insurancePolicyDTO.setComissarNumber(insurancePolicy.getComissarNumber());
        insurancePolicyDTO.setCompanyNumber(insurancePolicy.getCompanyNumber());
        insurancePolicyDTO.setCreatedAt(LocalDateTime.now().toString());
        insurancePolicyDTO.setUpdatedAt(LocalDateTime.now().toString());

        return insurancePolicyDTO;
    }

    public static InsurancePolicy toModel(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy insurancePolicy = new InsurancePolicy();
        insurancePolicy.setPolicyNumber(insurancePolicyRequestDTO.getPolicyNumber());
        insurancePolicy.setPolicyType(insurancePolicyRequestDTO.getPolicyType());
        insurancePolicy.setPolicyHolder(insurancePolicyRequestDTO.getPolicyHolder());
//        insurancePolicy.setPremium(BigDecimal.valueOf(Long.parseLong(insurancePolicyRequestDTO.getPremium())));
        insurancePolicy.setStartDate(LocalDate.parse(insurancePolicyRequestDTO.getStartDate()));
        insurancePolicy.setEndDate(LocalDate.parse(insurancePolicyRequestDTO.getEndDate()));
        insurancePolicy.setStatus(insurancePolicyRequestDTO.getStatus());
        insurancePolicy.setVehicleOwner(insurancePolicyRequestDTO.getVehicleOwner());
        insurancePolicy.setInsuredCar(insurancePolicyRequestDTO.getInsuredCar());
//        insurancePolicy.setDrivers(insurancePolicyRequestDTO.getDrivers());
        insurancePolicy.setComissarNumber(insurancePolicyRequestDTO.getComissarNumber());
        insurancePolicy.setCompanyNumber(insurancePolicyRequestDTO.getCompanyNumber());
        if (insurancePolicyRequestDTO.getDrivers() != null) {
            insurancePolicy.setDrivers(insurancePolicyRequestDTO.getDrivers().stream().map(driverRequest ->{
                Driver driver = new Driver();
                driver.setFullName(driverRequest.getFullName());
                driver.setBirthDate(driverRequest.getBirthDate());
                driver.setLicenseNumber(driverRequest.getLicenseNumber());
                driver.setDrivingExperience(driverRequest.getDrivingExperience());
                driver.setPolicy(insurancePolicy);
                return driver;
            })
            .collect(Collectors.toList()));
        }
        insurancePolicy.setUsagePurpose(insurancePolicyRequestDTO.getUsagePurpose());
        insurancePolicy.setMarketValue(insurancePolicyRequestDTO.getMarketValue());
        insurancePolicy.setFranchise(insurancePolicyRequestDTO.getFranchise());
        insurancePolicy.setIsLegalEntity(insurancePolicyRequestDTO.getIsLegalEntity());

        return insurancePolicy;
    }
}
