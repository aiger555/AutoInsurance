package com.ain.insuranceservice.dto.chat;

import lombok.Data;

@Data
public class ChatRequestDTO {
    private String sessionId;
    private String userId;
    private String message;
    private String policyNumber;
    private String language;
}