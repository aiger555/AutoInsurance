package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.ClientRepository;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.mappers.InsurancePolicyMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InsurancePolicyService {
    private final ClientRepository clientRepository;
    private InsurancePolicyRepository insurancePolicyRepository;

    public InsurancePolicyService(InsurancePolicyRepository insurancePolicyRepository, ClientRepository clientRepository) {
        this.insurancePolicyRepository = insurancePolicyRepository;
        this.clientRepository = clientRepository;
    }

    public List<InsurancePolicyResponseDTO> getCars() {
        List<InsurancePolicy> insurancePolicies = insurancePolicyRepository.findAll();
        return insurancePolicies.stream()
                .map(InsurancePolicyMapper::toDTO).toList();
    }


    public InsurancePolicyResponseDTO createInsurancePolicy(InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicy newInsurancePolicy = insurancePolicyRepository.save(InsurancePolicyMapper.toModel(insurancePolicyRequestDTO));
        return InsurancePolicyMapper.toDTO(newInsurancePolicy);

    }
}
