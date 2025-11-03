package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.InsurancePolicyService;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


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

    @PutMapping("/{policyNumber}")
    public ResponseEntity<InsurancePolicyResponseDTO> updatePolicy(@PathVariable String policyNumber, @Validated({Default.class}) @RequestBody InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicyResponseDTO insurancePolicyResponseDTO = policyService.updatePolicy(policyNumber, insurancePolicyRequestDTO);
        return new ResponseEntity<>(insurancePolicyResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}