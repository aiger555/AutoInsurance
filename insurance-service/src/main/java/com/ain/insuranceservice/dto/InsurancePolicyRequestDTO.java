package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.PolicyType;
import com.ain.insuranceservice.models.VehicleType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InsurancePolicyRequestDTO {
    @NotBlank(message = "Policy Number is required")
    private String policyNumber;

    @NotBlank(message = "Policy Type is required")
    private PolicyType policyType;

    @NotBlank(message = "Premium is required")
    private String premium;

    @NotBlank(message = "Start Date is required")
    private String startDate;

    @NotBlank(message = "End Date is required")
    private String endDate;

    @NotBlank(message = "status is required")
    private String status;

    @NotBlank(message = "Vehicle Owner is required")
    private ClientResponseDTO vehicleOwner;

    @NotBlank(message = "Insured Car is required")
    private CarResponseDTO insuredCar;

    private List<DriverResponseDTO> drivers = new ArrayList<>();

    private String comissarNumber;

    private String companyNumber;

    private String createdAt;

    private VehicleType updatedAt;
}
