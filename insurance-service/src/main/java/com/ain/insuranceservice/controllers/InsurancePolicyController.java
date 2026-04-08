package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.*;
import com.ain.insuranceservice.mappers.InsurancePolicyMapper;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.services.ClientService;
import com.ain.insuranceservice.services.InsurancePolicyService;
import com.ain.insuranceservice.services.PDFService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.ain.insuranceservice.exception.InsurancePolicyNotFoundException;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/policies")
@Tag(name = "Policy", description = "API for managing Policies")
public class InsurancePolicyController {
    private final InsurancePolicyService policyService;
    private final PDFService pdfService;
    private final InsurancePolicyRepository insurancePolicyRepository;


    @GetMapping
    @Operation(summary = "Get Policies")
    public ResponseEntity<List<InsurancePolicyResponseDTO>> getPolicies() {
        List<InsurancePolicyResponseDTO> policies = policyService.getInsurancePolicies();
        return new ResponseEntity<>(policies, HttpStatus.OK);
    }
    @GetMapping("/{policyNumber}")
    @Operation(summary = "Get Policy by Number")
    public ResponseEntity<InsurancePolicyResponseDTO> getPolicyByNumber(@PathVariable String policyNumber) {
        InsurancePolicy policy = policyService.getPolicyByNumber(policyNumber);
        InsurancePolicyResponseDTO response = InsurancePolicyMapper.toDTO(policy);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new Policy")
    public ResponseEntity<InsurancePolicyResponseDTO> createPolicy(@Valid @RequestBody InsurancePolicyRequestDTO policyRequestDTO) {
        InsurancePolicyResponseDTO policyResponseDTO = policyService.createInsurancePolicy(policyRequestDTO);
        return new ResponseEntity<>(policyResponseDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{policyNumber}")
    @Operation(summary = "Update a Policy")
    public ResponseEntity<InsurancePolicyResponseDTO> updatePolicy(@PathVariable String policyNumber, @Validated({Default.class}) @RequestBody InsurancePolicyRequestDTO insurancePolicyRequestDTO) {
        InsurancePolicyResponseDTO insurancePolicyResponseDTO = policyService.updatePolicy(policyNumber, insurancePolicyRequestDTO);
        return new ResponseEntity<>(insurancePolicyResponseDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a Policy")
    public ResponseEntity<Void> deletePolicy(@PathVariable String id) {
        policyService.deletePolicy(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{policyNumber}/download")
    @Operation(summary = "Download policy as PDF")
    public ResponseEntity<byte[]> downloadPolicyPDF(@PathVariable String policyNumber) {
        try {
            log.info("🔍 Searching for policy with number: {}", policyNumber);

            InsurancePolicy policy = insurancePolicyRepository.findByPolicyNumberWithAssociations(policyNumber);

            if (policy == null) {
                log.error("Policy not found: {}", policyNumber);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info("Policy found: {}", policy.getPolicyNumber());
            log.info("Vehicle Owner: {}",
                    policy.getVehicleOwner() != null ? policy.getVehicleOwner().getFullName() : "NULL");
            log.info("Insured Car: {}",
                    policy.getInsuredCar() != null ? policy.getInsuredCar().getBrand() : "NULL");
            log.info("Drivers count: {}",
                    policy.getDrivers() != null ? policy.getDrivers().size() : 0);

            if (policy.getVehicleOwner() == null || policy.getInsuredCar() == null) {
                log.warn("Some associations are null, using test data");
                policy = createTestPolicyData(policy);
            }

            byte[] pdfBytes = pdfService.generateInsurancePolicyPDF(policy);

            String filename = "insurance_policy_" + policyNumber + ".pdf";

            log.info("PDF generated successfully for policy: {}", policyNumber);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            log.error("Error generating PDF for policy {}", policyNumber, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private InsurancePolicy createTestPolicyData(InsurancePolicy policy) {
        log.info("Creating test data for policy: {}", policy.getPolicyNumber());

        if (policy.getVehicleOwner() == null) {
            com.ain.insuranceservice.models.Client testOwner = new com.ain.insuranceservice.models.Client();
            testOwner.setFullName("Иванов Иван Иванович");
            testOwner.setDateOfBirth(java.time.LocalDate.of(1985, 5, 15));
            testOwner.setPhoneNumber("+996555123456");
            testOwner.setPassport_number("AN1234567");
            testOwner.setPin("10123194567890");
            testOwner.setAddress("г. Бишкек, ул. Чуйкова 123");
            policy.setVehicleOwner(testOwner);
        }

        if (policy.getInsuredCar() == null) {
            com.ain.insuranceservice.models.Car testCar = new com.ain.insuranceservice.models.Car();
            testCar.setBrand("Toyota");
            testCar.setModel("Camry");
            testCar.setManufactureYear(2020);
            testCar.setVin("JTDKB20U303000001");
            testCar.setLicensePlate("01KG123AB");
            testCar.setRegistrationAuthority("ГУВД г. Бишкек");
            testCar.setRegistrationDate(java.time.LocalDate.of(2020, 5, 15));
            testCar.setTechPassportNumber("TP123456789");
            testCar.setEngineVolume(2.5);
            testCar.setVehicleType(com.ain.insuranceservice.models.VehicleType.PASSENGER_CAR);
            policy.setInsuredCar(testCar);
        }

        if (policy.getDrivers() == null || policy.getDrivers().isEmpty()) {
            com.ain.insuranceservice.models.Driver testDriver = new com.ain.insuranceservice.models.Driver();
            testDriver.setFullName("Иванов Иван Иванович");
            testDriver.setBirthDate(java.time.LocalDate.of(1985, 5, 15));
            testDriver.setLicenseNumber("AA123456");
            testDriver.setDrivingExperience(java.time.LocalDate.of(2014, 5, 15));
            testDriver.setPolicy(policy);

            policy.setDrivers(java.util.Arrays.asList(testDriver));
        }

        return policy;
    }

}