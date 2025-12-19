package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class InsurancePolicyRequestDTO {
    @NotBlank(message = "Policy Number is required")
    private String policyNumber;

    @NotNull(message = "Policy Type is required")
    private PolicyType policyType;

    private String policyHolder;

    private BigDecimal premium;

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

    @Pattern(regexp = "personal|rent", message = "usage purpose must be 'personal' or 'rental'")
    private String usagePurpose;
    private BigDecimal marketValue;
    private BigDecimal franchise;
    private Boolean isLegalEntity; //for phys. or yur.

}
