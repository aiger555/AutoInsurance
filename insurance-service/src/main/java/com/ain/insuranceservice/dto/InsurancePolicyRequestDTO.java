package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InsurancePolicyRequestDTO {
    @NotBlank(message = "Policy Number is required")
    private String policyNumber;

    @NotNull(message = "Policy Type is required")
    private PolicyType policyType;

    private String premium;

    @NotBlank(message = "Start Date is required")
    private String startDate;

    @NotBlank(message = "End Date is required")
    private String endDate;

    @NotBlank(message = "status is required")
    private String status;

    @NotNull(message = "Vehicle Owner is required")
    private Client vehicleOwner;

    @NotNull(message = "Insured Car is required")
    private Car insuredCar;

    private List<Driver> drivers = new ArrayList<>();

    private String comissarNumber;

    private String companyNumber;

}
