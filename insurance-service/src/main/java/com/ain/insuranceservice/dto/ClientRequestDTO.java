package com.ain.insuranceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ClientRequestDTO {
    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Birth date is required")
    private String dateOfBirth;

    @NotBlank(message = "Phone number is required")
    private String phoneNumber;

    @NotBlank(message = "Passport number is required")
    private String passportNumber;

    @NotBlank(message = "PIN is required")
    private String pin;

    @NotBlank(message = "Address is required")
    private String address;

}
