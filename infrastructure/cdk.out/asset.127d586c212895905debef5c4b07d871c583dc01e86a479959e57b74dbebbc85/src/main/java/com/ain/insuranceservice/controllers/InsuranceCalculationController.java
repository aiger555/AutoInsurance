//package com.ain.insuranceservice.controllers;
//
//import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
//import com.ain.insuranceservice.models.InsurancePolicy;
//import com.ain.insuranceservice.services.InsurancePolicyService;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.math.BigDecimal;
//
//@RestController
//@RequestMapping("/calculate")
//public class InsuranceCalculationController {
//    private InsurancePolicyService insurancePolicyService;
//
//    @PostMapping
//    public ResponseEntity<BigDecimal> calculatePremium(@RequestBody InsurancePolicyRequestDTO requestDTO) {
//        BigDecimal premium = insurancePolicyService.calculatePremium(requestDTO);
//        return ResponseEntity.ok(premium);
//    }
//
//
//}
