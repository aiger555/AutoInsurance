package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.DriverRequestDTO;
import com.ain.insuranceservice.dto.DriverResponseDTO;
import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import com.ain.insuranceservice.exception.*;
import com.ain.insuranceservice.mappers.DriverMapper;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.repositories.ClientRepository;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.mappers.InsurancePolicyMapper;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.services.InsuranceCalculationService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InsurancePolicyService {
    private final InsurancePolicyRepository insurancePolicyRepository;

    @Autowired
    private final InsuranceCalculationService calculationService;


    public List<InsurancePolicyResponseDTO> getInsurancePolicies() {
        List<InsurancePolicy> insurancePolicies = insurancePolicyRepository.findAll();
        return insurancePolicies.stream()
                .map(InsurancePolicyMapper::toDTO).toList();
    }

    @Transactional
    public InsurancePolicyResponseDTO createInsurancePolicy(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        if (insurancePolicyRepository.existsByPolicyNumber(insurancePolicyRequestDTO.getPolicyNumber())) {
            throw new PolicyNumberAlreadyExistsException("A policy with this number already exists" + insurancePolicyRequestDTO.getPolicyNumber());
        }
        InsurancePolicy newInsurancePolicy = InsurancePolicyMapper.toModel(insurancePolicyRequestDTO);

        if(newInsurancePolicy.getVehicleOwner() != null) {
            newInsurancePolicy.getVehicleOwner().getFullName();
        }

        BigDecimal calculatedPremium = calculatePremiumBasedOnPolicyType(newInsurancePolicy, insurancePolicyRequestDTO);
        newInsurancePolicy.setPremium(calculatedPremium);

        InsurancePolicy savedPolicy = insurancePolicyRepository.save(newInsurancePolicy);
        return InsurancePolicyMapper.toDTO(savedPolicy);
    }


    private BigDecimal calculatePremiumBasedOnPolicyType(InsurancePolicy insurancePolicy, InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        return switch (insurancePolicy.getPolicyType()) {
            case OSAGO, DSAGO -> calculationService.calculatePremium(insurancePolicy);
            case CASCO -> {
                String usagePurpose = getCascoUsagePurpose(insurancePolicyRequestDTO);
                BigDecimal marketValue = getCascoMarketValue(insurancePolicyRequestDTO);
                BigDecimal franchise = getCascoFranchise(insurancePolicyRequestDTO);
                boolean isLegalEntity = getCascoIsLegalEntity(insurancePolicyRequestDTO);

                yield calculationService.calculateCascoPremium(
                        insurancePolicy.getInsuredCar(),
                        usagePurpose,
                        marketValue,
                        franchise,
                        isLegalEntity
                );
            }
            default -> throw new IllegalStateException("Unexpected policy type: " + insurancePolicy.getPolicyType());
        };
    }


    private String getCascoUsagePurpose(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        if (insurancePolicyRequestDTO.getUsagePurpose() != null && !insurancePolicyRequestDTO.getUsagePurpose().trim().isEmpty()) {
            String usagePurpose = insurancePolicyRequestDTO.getUsagePurpose().toLowerCase();
            if (usagePurpose.equals("personal") || usagePurpose.equals("rent")) {
                return usagePurpose;
            } else {
                throw new IllegalArgumentException("Invalid usagePurpose value: " + usagePurpose);
            }
        }
        return "personal";
    }

    private BigDecimal getCascoMarketValue(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        if (insurancePolicyRequestDTO.getMarketValue() != null) {
            if (insurancePolicyRequestDTO.getMarketValue().compareTo(BigDecimal.ZERO) > 0) {
                return insurancePolicyRequestDTO.getMarketValue();
            } else {
                throw new IllegalArgumentException("Invalid market value, it shoulb be greater than 0");
            }
        }
        return new BigDecimal("500000");
    }

    private BigDecimal getCascoFranchise(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        if (insurancePolicyRequestDTO.getFranchise() != null) {
            if (insurancePolicyRequestDTO.getFranchise().compareTo(BigDecimal.ZERO) >= 0) {
                return insurancePolicyRequestDTO.getFranchise();
            } else {
                throw new IllegalArgumentException("Invalid franchise value, it should not be less than 0");
            }
        }
        return new BigDecimal("4375");
    }

    private boolean getCascoIsLegalEntity(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        if (insurancePolicyRequestDTO.getIsLegalEntity() != null) {
            return insurancePolicyRequestDTO.getIsLegalEntity();
        }
        return false;
    }

    public InsurancePolicyResponseDTO updatePolicy(String policyNumber, InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy insurancePolicy = insurancePolicyRepository.findById(policyNumber).orElseThrow(
                () -> new InsurancePolicyNotFoundException("Insurance Policy not found with ID: " + policyNumber));

        if (!policyNumber.equals(insurancePolicyRequestDTO.getPolicyNumber()) && insurancePolicyRepository.existsByPolicyNumber(
                insurancePolicyRequestDTO.getPolicyNumber()
        )) {
            throw new PolicyNumberAlreadyExistsException("A policy with this id.number already exists" + insurancePolicyRequestDTO.getPolicyNumber());
        }

        insurancePolicy.setPolicyNumber(insurancePolicyRequestDTO.getPolicyNumber());
        insurancePolicy.setPolicyType(insurancePolicyRequestDTO.getPolicyType());
        insurancePolicy.setPolicyHolder(insurancePolicyRequestDTO.getPolicyHolder());
//        insurancePolicy.setPremium(insurancePolicyRequestDTO.getPremium());
        insurancePolicy.setStartDate(LocalDate.parse(insurancePolicyRequestDTO.getStartDate()));
        insurancePolicy.setEndDate(LocalDate.parse(insurancePolicyRequestDTO.getEndDate()));
        insurancePolicy.setStatus(insurancePolicyRequestDTO.getStatus());
        insurancePolicy.setVehicleOwner(insurancePolicyRequestDTO.getVehicleOwner());
        insurancePolicy.setInsuredCar(insurancePolicyRequestDTO.getInsuredCar());
//        insurancePolicy.setDrivers(insurancePolicyRequestDTO.getDrivers());
        insurancePolicy.setComissarNumber(insurancePolicyRequestDTO.getComissarNumber());
        insurancePolicy.setCompanyNumber(insurancePolicyRequestDTO.getCompanyNumber());
        insurancePolicy.setUsagePurpose(insurancePolicyRequestDTO.getUsagePurpose());
        insurancePolicy.setMarketValue(insurancePolicyRequestDTO.getMarketValue());
        insurancePolicy.setFranchise(insurancePolicyRequestDTO.getFranchise());
        insurancePolicy.setIsLegalEntity(insurancePolicyRequestDTO.getIsLegalEntity());

        insurancePolicy.getDrivers().clear();
        if (insurancePolicyRequestDTO.getDrivers() != null) {
            for (Driver driver : insurancePolicyRequestDTO.getDrivers()) {
                driver.setPolicy(insurancePolicy);
                insurancePolicy.getDrivers().add(driver);
            }
        }

        BigDecimal calculatedPremium = calculatePremiumBasedOnPolicyType(insurancePolicy, insurancePolicyRequestDTO);
        insurancePolicy.setPremium(calculatedPremium);

        insurancePolicy.setUpdatedAt(LocalDateTime.now());

        InsurancePolicy updatedPolicy = insurancePolicyRepository.save(insurancePolicy);
        return InsurancePolicyMapper.toDTO(updatedPolicy);

    }

}
