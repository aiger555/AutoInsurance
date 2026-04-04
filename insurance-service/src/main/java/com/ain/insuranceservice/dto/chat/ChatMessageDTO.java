package com.ain.insuranceservice.dto.chat;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatMessageDTO {
    private String id;
    private String sessionId;
    private String sender;
    private String message;
    private LocalDateTime timestamp;
    private MessageType type;
    private List<ActionButton> actions;

    public enum MessageType {
        TEXT, QUICK_REPLY, POLICY_INFO, CLAIM_GUIDE, PAYMENT_LINK, EMERGENCY
    }

    @Data
    public static class ActionButton {
        private String text;
        private String action;
        private String payload;
    }
}