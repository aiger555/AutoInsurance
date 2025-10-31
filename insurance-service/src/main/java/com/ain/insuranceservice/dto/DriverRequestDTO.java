package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.InsurancePolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DriverRequestDTO {
    @NotNull(message = "Insurance policy is required")
    private InsurancePolicy policy;

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Birth date is required")
    private String birthDate;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Driving Experience is required")
    private String drivingExperience;

}
