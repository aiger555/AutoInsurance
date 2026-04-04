package com.ain.insuranceservice.dto.chat;

import com.ain.insuranceservice.dto.InsurancePolicyResponseDTO;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ChatResponseDTO {
    private String sessionId;
    private String message;
    private String intent;
    private double confidence;
    private List<ChatMessageDTO.ActionButton> suggestions;
    private List<InsurancePolicyResponseDTO> relatedPolicies;
    private boolean requiresHumanAgent;
    private String emotion;
}