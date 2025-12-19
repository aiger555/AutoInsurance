package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.PolicyType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class InsurancePolicyResponseDTO {
    private String policyNumber;
    private PolicyType policyType;
    private String policyHolder;
    private String premium;
    private String startDate;
    private String endDate;
    private String status;
    private ClientResponseDTO vehicleOwner;
    private CarResponseDTO insuredCar;
    private List<DriverResponseDTO> drivers = new ArrayList<>();
    private String comissarNumber;
    private String companyNumber;
    private String createdAt;
    private String updatedAt;
    private String usagePurpose;
    private String marketValue;
    private String franchise;
    private String isLegalEntity;

}
