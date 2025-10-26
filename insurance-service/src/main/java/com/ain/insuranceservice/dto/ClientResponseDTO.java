package com.ain.insuranceservice.dto;

import lombok.Data;

@Data
public class ClientResponseDTO {
    private String id;
    private String fullname;
    private String dateOfBirth;
    private String phoneNumber;
    private String passportNumber;
    private String pin;
    private String address;
}
