package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.InsurancePolicy;
import lombok.Data;

@Data
public class DriverResponseDTO {
    private String id;
    private String policyNumber;
    private String fullName;
    private String birthDate;
    private String licenseNumber;
    private String drivingExperience;
}
