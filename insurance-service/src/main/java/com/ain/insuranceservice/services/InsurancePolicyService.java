package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.mappers.InsurancePolicyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePolicyService {
    private InsurancePolicyRepository insurancePolicyRepository;

    public InsurancePolicyService(InsurancePolicyRepository insurancePolicyRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
    }

    public List<InsurancePolicyResponseDTO> getCars() {
        List<InsurancePolicy> insurancePolicies = insurancePolicyRepository.findAll();
        return insurancePolicies.stream()
                .map(InsurancePolicyMapper::toDTO).toList();
    }
}
