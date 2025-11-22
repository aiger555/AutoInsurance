package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.InsurancePolicyService;
import com.ain.insuranceservice.services.PDFService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/policies")
@Tag(name = "Policy", description = "API for managing Policies") // swagger
public class InsurancePolicyController {
    private final InsurancePolicyService policyService;
    private final PDFService pdfService;

    @GetMapping
    @Operation(summary = "Get Policies") // swagger
    public ResponseEntity<List<InsurancePolicyResponseDTO>> getPolicies() {
        List<InsurancePolicyResponseDTO> policies = policyService.getInsurancePolicies();
        return new ResponseEntity<>(policies, HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Create a new Policy") // swagger
    public ResponseEntity<InsurancePolicyResponseDTO> createPolicy(@Valid @RequestBody InsurancePolicyRequestDTO policyRequestDTO) {
        InsurancePolicyResponseDTO policyResponseDTO = policyService.createInsurancePolicy(policyRequestDTO);
        return new ResponseEntity<>(policyResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{policyNumber}")
    @Operation(summary = "Update a Policy") // swagger
    public ResponseEntity<InsurancePolicyResponseDTO> updatePolicy(@PathVariable String policyNumber, @Validated({Default.class}) @RequestBody InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicyResponseDTO insurancePolicyResponseDTO = policyService.updatePolicy(policyNumber, insurancePolicyRequestDTO);
        return new ResponseEntity<>(insurancePolicyResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Policy") // swagger
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{policyNumber}/download")
    @Operation(summary = "Download policy as PDF")
    public ResponseEntity<byte[]> downloadPolicyPDF(@PathVariable String policyNumber) {
        try {
            InsurancePolicy policy = policyService.getPolicyByNumber(policyNumber);
            byte[] pdfBytes = pdfService.generateInsurancePolicyPDF(policy);

            String filename = "insurance_policy_" + policyNumber + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"" )
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);
        } catch (Exception e){
            log.error("Error generating PDF for policy {}", policyNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}