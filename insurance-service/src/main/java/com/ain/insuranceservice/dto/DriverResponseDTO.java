package com.ain.insuranceservice.dto;

import lombok.Data;

@Data
public class DriverResponseDTO {
    private String id;
    private String fullName;
    private String birthDate;
    private String licenseNumber;
    private String drivingExperience;
}
