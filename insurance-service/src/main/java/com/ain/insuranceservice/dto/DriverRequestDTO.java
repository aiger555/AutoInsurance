package com.ain.insuranceservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DriverRequestDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Birth date is required")
    private String birthDate;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    @NotBlank(message = "Driving Experience is required")
    private String drivingExperience;

}
