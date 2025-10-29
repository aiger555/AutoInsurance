package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.InsurancePolicyService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Data
@RestController
@RequestMapping("/policies")
public class InsurancePolicyController {
    private final InsurancePolicyService policyService;

    @GetMapping
    public ResponseEntity<List<InsurancePolicyResponseDTO>> getPolicies() {
        List<InsurancePolicyResponseDTO> policies = policyService.getInsurancePolicies();
        return new ResponseEntity<>(policies, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<InsurancePolicyResponseDTO> createPolicy(@Valid @RequestBody InsurancePolicyRequestDTO policyRequestDTO) {
        InsurancePolicyResponseDTO policyResponseDTO = policyService.createInsurancePolicy(policyRequestDTO);
        return new ResponseEntity<>(policyResponseDTO, HttpStatus.CREATED);
    }
}