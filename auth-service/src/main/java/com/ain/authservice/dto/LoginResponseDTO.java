package com.ain.authservice.dto;

import lombok.Data;

@Data
public class LoginResponseDTO {
    private final String token;
    private String error;


}
