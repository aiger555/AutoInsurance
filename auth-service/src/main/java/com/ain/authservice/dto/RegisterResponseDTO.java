package com.ain.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RegisterResponseDTO {
    private UUID id;
    private String email;
    private String role;
    private String message;
}