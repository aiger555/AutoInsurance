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
    private CarRepository carRepository;

    @Autowired
    private InsuranceCalculationService calculationService;


    public InsurancePolicyService(InsurancePolicyRepository insurancePolicyRepository, ClientRepository clientRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.clientRepository = clientRepository;
    }

    public List<InsurancePolicyResponseDTO> getInsurancePolicies() {
        List<InsurancePolicy> insurancePolicies = insurancePolicyRepository.findAll();
        return insurancePolicies.stream()
                .map(InsurancePolicyMapper::toDTO).toList();
    }

    public InsurancePolicyResponseDTO createInsurancePolicy(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy newInsurancePolicy = insurancePolicyRepository.save(InsurancePolicyMapper.toModel(insurancePolicyRequestDTO));
        BigDecimal calculatedPremium = calculationService.calculatePremium(newInsurancePolicy);
        newInsurancePolicy.setPremium(calculatedPremium);
//
//        if (newInsurancePolicy.getDrivers() != null) {
//            for (Driver driver : newInsurancePolicy.getDrivers()) {
//                driver.setPolicy(newInsurancePolicy);
//            }
//        }

        InsurancePolicy savedPolicy = insurancePolicyRepository.save(newInsurancePolicy);
        return InsurancePolicyMapper.toDTO(savedPolicy);
    }

    public BigDecimal calculatePremium(InsurancePolicyRequestDTO requestDTO) {
        InsurancePolicy tempPolicy = InsurancePolicyMapper.toModel(requestDTO);
        return calculationService.calculatePremium(tempPolicy);
    }

    public BigDecimal calculateCascoPremium(Car car, String usagePurpose, BigDecimal marketValue,
                                            BigDecimal franchise, boolean isLegalEntity) {
        return calculationService.calculateCascoPremium(car, usagePurpose, marketValue, franchise, isLegalEntity);
    }

}
