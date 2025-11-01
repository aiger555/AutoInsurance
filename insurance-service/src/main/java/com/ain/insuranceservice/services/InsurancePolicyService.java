package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.services.InsuranceCalculationService;

import java.math.BigDecimal;
import java.util.List;

@Data
@Service
public class InsurancePolicyService {
    private final ClientRepository clientRepository;
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final CarRepository carRepository;

    @Autowired
    private InsuranceCalculationService calculationService;


    public InsurancePolicyService(InsurancePolicyRepository insurancePolicyRepository, ClientRepository clientRepository, CarRepository carRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.clientRepository = clientRepository;
        this.carRepository = carRepository;
    }

    public List<InsurancePolicyResponseDTO> getInsurancePolicies() {
        List<InsurancePolicy> insurancePolicies = insurancePolicyRepository.findAll();
        return insurancePolicies.stream()
                .map(InsurancePolicyMapper::toDTO).toList();
    }

    public InsurancePolicyResponseDTO createInsurancePolicy(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy newInsurancePolicy = insurancePolicyRepository.save(InsurancePolicyMapper.toModel(insurancePolicyRequestDTO));
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




//    public BigDecimal calculateCascoPremiumWithAdditionalData(InsurancePolicy insurancePolicy, InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
//        if (insurancePolicyRequestDTO.getMarketValue() == null) {
//            throw new IllegalArgumentException("Market value is required for CASCO policy");
//        }
//
//        if (insurancePolicyRequestDTO.getUsagePurpose() == null || insurancePolicyRequestDTO.getUsagePurpose().trim().isEmpty()) {
//            throw new IllegalArgumentException("Usage purpose is required for CASCO policy");
//        }
//
//        String usagePurpose = getCascoUsagePurpose(insurancePolicyRequestDTO);
//        BigDecimal marketValue = getCascoMarketValue(insurancePolicyRequestDTO);
//        BigDecimal franchise = getCascoFranchise(insurancePolicyRequestDTO);
//        boolean isLegalEntity = getCascoIsLegalEntity(insurancePolicyRequestDTO);
//
//        BigDecimal basePremium = calculationService.calculateCascoPremium(
//                insurancePolicy.getInsuredCar(),
//                usagePurpose,
//                marketValue,
//                franchise,
//                isLegalEntity
//        );
//
//        if ("rent".equals(usagePurpose)) {
//            BigDecimal rentSurCharge = basePremium.multiply(new BigDecimal("0.10"));
//            basePremium = basePremium.add(rentSurCharge);
//        }
//        return basePremium;
//    }
//

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

    public BigDecimal calculatePremium(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy tempPolicy = InsurancePolicyMapper.toModel(insurancePolicyRequestDTO);
        return calculatePremiumBasedOnPolicyType(tempPolicy, insurancePolicyRequestDTO);
    }

    public BigDecimal calculateCascoPremium(Car car, String usagePurpose, BigDecimal marketValue, BigDecimal franchise, boolean isLegalEntity) {
        return calculationService.calculateCascoPremium(car, usagePurpose, marketValue, franchise, isLegalEntity);
    }

}
