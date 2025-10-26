package com.ain.insuranceservice.mappers;


import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import com.ain.insuranceservice.models.InsurancePolicy;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;


@Data
public class InsurancePolicyMapper {
    private final ClientMapper clientMapper;
    private final CarMapper carMapper;
    private final DriverMapper driverMapper;

    public static InsurancePolicyResponseDTO toDTO(InsurancePolicy insurancePolicy) {
        InsurancePolicyResponseDTO insurancePolicyDTO = new InsurancePolicyResponseDTO();
        insurancePolicyDTO.setPolicyNumber(insurancePolicy.getPolicyNumber());
        insurancePolicyDTO.setPolicyType(insurancePolicy.getPolicyType());
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
        insurancePolicyDTO.setCreatedAt(insurancePolicy.getCreatedAt().toString());
        insurancePolicyDTO.setUpdatedAt(insurancePolicy.getUpdatedAt().toString());

        return insurancePolicyDTO;
    }
}
