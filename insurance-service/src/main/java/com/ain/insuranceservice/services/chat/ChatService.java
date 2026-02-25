package com.ain.insuranceservice.services.chat;

import com.ain.insuranceservice.dto.chat.ChatRequestDTO;
import com.ain.insuranceservice.dto.chat.ChatResponseDTO;
import com.ain.insuranceservice.dto.chat.ChatMessageDTO.ActionButton;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.services.InsurancePolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final IntentClassifier intentClassifier;
    private final InsurancePolicyRepository policyRepository;
    private final InsurancePolicyService policyService;

    private final Map<String, ConversationContext> userSessions = new HashMap<>();

    public ChatResponseDTO processMessage(ChatRequestDTO request) {
        String sessionId = getOrCreateSession(request);
        ConversationContext context = userSessions.get(sessionId);

        log.info("Processing message for session {}: {}", sessionId, request.getMessage());

        IntentClassifier.IntentClassification classification =
                intentClassifier.classify(request.getMessage());

        ChatResponseDTO response = switch (classification.getIntent()) {
            case "GREETING" -> handleGreeting(context);
            case "POLICY_INQUIRY" -> handlePolicyInquiry(request, context);
            case "CLAIM_FILING" -> handleClaimFiling(context);
            case "RENEWAL" -> handleRenewal(request, context);
            case "PAYMENT" -> handlePayment(request, context);
            case "EMERGENCY" -> handleEmergency();
            case "FAREWELL" -> handleFarewell(context);
            case "HELP" -> handleHelp();
            default -> handleUnknown(classification.getDefaultResponse());
        };

        updateContext(sessionId, request.getMessage(), response);

        return response;
    }

    private ChatResponseDTO handleGreeting(ConversationContext context) {
        String userName = context.getUserName() != null ? context.getUserName() : "there";

        List<ActionButton> suggestions = Arrays.asList(
                createButton("Check My Policy", "CHECK_POLICY", null),
                createButton("File a Claim", "FILE_CLAIM", null),
                createButton("Make Payment", "MAKE_PAYMENT", null),
                createButton("Emergency Help", "EMERGENCY", null)
        );

        return ChatResponseDTO.builder()
                .sessionId(context.getSessionId())
                .message(String.format("Hello %s! How can I assist you with your insurance today?", userName))
                .intent("GREETING")
                .confidence(1.0)
                .suggestions(suggestions)
                .build();
    }

    private ChatResponseDTO handlePolicyInquiry(ChatRequestDTO request, ConversationContext context) {
        String policyNumber = extractPolicyNumber(request.getMessage());

        if (policyNumber == null && context.getLastPolicyNumber() != null) {
            policyNumber = context.getLastPolicyNumber();
        }

        if (policyNumber != null) {
            try {
                InsurancePolicy policy = policyService.getPolicyByNumber(policyNumber);

                List<ActionButton> actions = Arrays.asList(
                        createButton("View Details", "VIEW_POLICY", policyNumber),
                        createButton("Download PDF", "DOWNLOAD_POLICY", policyNumber),
                        createButton("Renew Policy", "RENEW_POLICY", policyNumber)
                );

                String policyInfo = String.format(
                        "Policy #%s\n" +
                                "Type: %s\n" +
                                "Status: %s\n" +
                                "Valid: %s to %s\n" +
                                "Premium: %s\n" +
                                "Vehicle: %s %s",
                        policy.getPolicyNumber(),
                        policy.getPolicyType(),
                        policy.getStatus(),
                        policy.getStartDate(),
                        policy.getEndDate(),
                        policy.getPremium(),
                        policy.getInsuredCar().getBrand(),
                        policy.getInsuredCar().getModel()
                );

                return ChatResponseDTO.builder()
                        .sessionId(context.getSessionId())
                        .message(policyInfo)
                        .intent("POLICY_INQUIRY")
                        .confidence(0.95)
                        .suggestions(actions)
                        .build();

            } catch (Exception e) {
                return ChatResponseDTO.builder()
                        .sessionId(context.getSessionId())
                        .message("I couldn't find a policy with number: " + policyNumber)
                        .intent("POLICY_INQUIRY")
                        .confidence(0.8)
                        .build();
            }
        } else {
            return ChatResponseDTO.builder()
                    .sessionId(context.getSessionId())
                    .message("To check your policy information, please provide your policy number.")
                    .intent("POLICY_INQUIRY")
                    .confidence(0.7)
                    .suggestions(Arrays.asList(
                            createButton("I don't know my policy number", "FIND_POLICY", null)
                    ))
                    .build();
        }
    }

    private ChatResponseDTO handleClaimFiling(ConversationContext context) {
        List<ActionButton> steps = Arrays.asList(
                createButton("Start Claim Process", "START_CLAIM", null),
                createButton("Check Claim Status", "CLAIM_STATUS", null),
                createButton("Required Documents", "CLAIM_DOCS", null)
        );

        return ChatResponseDTO.builder()
                .sessionId(context.getSessionId())
                .message("I'll help you file a claim. Here's what we need:\n\n" +
                        "1. Policy number\n" +
                        "2. Date and time of incident\n" +
                        "3. Location of incident\n" +
                        "4. Description of what happened\n" +
                        "5. Photos (if available)\n\n" +
                        "Would you like to start the claim process now?")
                .intent("CLAIM_FILING")
                .confidence(0.9)
                .suggestions(steps)
                .build();
    }

    private ChatResponseDTO handleRenewal(ChatRequestDTO request, ConversationContext context) {
        String policyNumber = extractPolicyNumber(request.getMessage());

        if (policyNumber != null || context.getLastPolicyNumber() != null) {
            policyNumber = policyNumber != null ? policyNumber : context.getLastPolicyNumber();

            return ChatResponseDTO.builder()
                    .sessionId(context.getSessionId())
                    .message("Your policy is eligible for renewal. Would you like to proceed?")
                    .intent("RENEWAL")
                    .confidence(0.9)
                    .suggestions(Arrays.asList(
                            createButton("Yes, renew now", "RENEW_NOW", policyNumber),
                            createButton("Show renewal terms", "RENEWAL_TERMS", policyNumber),
                            createButton("Not now", "CANCEL", null)
                    ))
                    .build();
        }

        return ChatResponseDTO.builder()
                .sessionId(context.getSessionId())
                .message("To help with renewal, please provide your policy number.")
                .intent("RENEWAL")
                .confidence(0.7)
                .build();
    }

    private ChatResponseDTO handlePayment(ChatRequestDTO request, ConversationContext context) {
        List<ActionButton> paymentOptions = Arrays.asList(
                createButton("Pay Now", "MAKE_PAYMENT", null),
                createButton("Payment History", "PAYMENT_HISTORY", null),
                createButton("Set Auto-pay", "AUTO_PAY", null)
        );

        return ChatResponseDTO.builder()
                .sessionId(context.getSessionId())
                .message("I can help you with payments. What would you like to do?")
                .intent("PAYMENT")
                .confidence(0.9)
                .suggestions(paymentOptions)
                .build();
    }

    private ChatResponseDTO handleEmergency() {
        return ChatResponseDTO.builder()
                .sessionId(UUID.randomUUID().toString())
                .message("🚨 EMERGENCY ASSISTANCE\n\n" +
                        "24/7 Emergency Hotline: +996 XXX XXX XXX\n\n" +
                        "For immediate assistance:\n" +
                        "• Roadside assistance: +996 XXX XXX XXX\n" +
                        "• Ambulance: 103\n" +
                        "• Police: 102\n\n" +
                        "Please stay calm. Help is on the way.")
                .intent("EMERGENCY")
                .confidence(1.0)
                .requiresHumanAgent(true)
                .build();
    }

    private ChatResponseDTO handleFarewell(ConversationContext context) {
        userSessions.remove(context.getSessionId());

        return ChatResponseDTO.builder()
                .sessionId(context.getSessionId())
                .message("Thank you for chatting with us! Have a great day!")
                .intent("FAREWELL")
                .confidence(1.0)
                .build();
    }

    private ChatResponseDTO handleHelp() {
        return ChatResponseDTO.builder()
                .sessionId(UUID.randomUUID().toString())
                .message("I can help you with:\n\n" +
                        "• Policy information - Ask about your policy details\n" +
                        "• Claims - File or check claim status\n" +
                        "• Renewals - Renew your policy\n" +
                        "• Payments - Make payments or check payment history\n" +
                        "• Emergency - Get immediate assistance\n\n" +
                        "What would you like help with?")
                .intent("HELP")
                .confidence(1.0)
                .build();
    }

    private ChatResponseDTO handleUnknown(String defaultResponse) {
        return ChatResponseDTO.builder()
                .sessionId(UUID.randomUUID().toString())
                .message(defaultResponse + "\n\nYou can also type 'help' to see what I can do.")
                .intent("UNKNOWN")
                .confidence(0.3)
                .build();
    }

    private String extractPolicyNumber(String message) {
        Pattern policyPattern = Pattern.compile("\\b(POL[-:]?)?(\\d{6,10})\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = policyPattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    private ActionButton createButton(String text, String action, String payload) {
        ActionButton button = new ActionButton();
        button.setText(text);
        button.setAction(action);
        button.setPayload(payload);
        return button;
    }

    private String getOrCreateSession(ChatRequestDTO request) {
        if (request.getSessionId() != null && userSessions.containsKey(request.getSessionId())) {
            return request.getSessionId();
        }

        String sessionId = UUID.randomUUID().toString();
        ConversationContext context = new ConversationContext();
        context.setSessionId(sessionId);
        context.setUserId(request.getUserId());
        context.setStartTime(LocalDateTime.now());

        userSessions.put(sessionId, context);
        return sessionId;
    }

    private void updateContext(String sessionId, String userMessage, ChatResponseDTO botResponse) {
        ConversationContext context = userSessions.get(sessionId);
        if (context != null) {
            context.setLastMessage(userMessage);
            context.setLastResponse(botResponse.getMessage());
            context.setLastIntent(botResponse.getIntent());
            context.setLastInteraction(LocalDateTime.now());

            String policyNumber = extractPolicyNumber(userMessage);
            if (policyNumber != null) {
                context.setLastPolicyNumber(policyNumber);
            }
        }
    }

    @lombok.Data
    private static class ConversationContext {
        private String sessionId;
        private String userId;
        private String userName;
        private LocalDateTime startTime;
        private LocalDateTime lastInteraction;
        private String lastMessage;
        private String lastResponse;
        private String lastIntent;
        private String lastPolicyNumber;
        private Map<String, Object> contextData = new HashMap<>();
    }
}