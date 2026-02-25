package com.ain.insuranceservice.services.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.regex.Pattern;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class IntentClassifier {

    private final Map<String, List<Pattern>> intentPatterns = new HashMap<>();
    private final Map<String, String> intentResponses = new HashMap<>();
    private final Map<String, List<String>> intentKeywords = new HashMap<>();
    private final Map<String, List<String>> intentEntities = new HashMap<>();

    public IntentClassifier() {
        initializeIntents();
        initializeKeywords();
        initializeEntities();
    }

    private void initializeIntents() {
        // GREETING intent
        intentPatterns.put("GREETING", Arrays.asList(
                Pattern.compile(".*\\b(hello|hi|hey|greetings|good morning|good afternoon|good evening)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile("^hi$|^hello$|^hey$", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\bhowdy\\b.*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("GREETING", "Hello! I'm your insurance assistant. How can I help you today?");

        // POLICY_INQUIRY intent
        intentPatterns.put("POLICY_INQUIRY", Arrays.asList(
                Pattern.compile(".*\\b(policy|coverage|insured|insurance|premium|deductible)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*how much (is|are).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*what (is|are) my.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*tell me about (my |the )?policy.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*show (my )?policy.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*details? of (my )?policy.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*policy (number|info|information).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("POLICY_INQUIRY", "I can help you with policy information. Could you provide your policy number?");

        // CLAIM_FILING intent
        intentPatterns.put("CLAIM_FILING", Arrays.asList(
                Pattern.compile(".*\\b(claim|accident|damage|crash|hit|stolen|theft|break\\-in)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*how (to|do I) file (a )?claim.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*report (an? )?(accident|damage|incident).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*submit (a )?claim.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*make (a )?claim.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*claim (status|process|procedure).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("CLAIM_FILING", "I'll help you file a claim. Let me guide you through the process...");

        // RENEWAL intent
        intentPatterns.put("RENEWAL", Arrays.asList(
                Pattern.compile(".*\\b(renew|renewal|expir|expire|expiration|extend|continue)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*when does my policy expire.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*how to renew.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*renew (my )?policy.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*policy (renewal|expiration).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("RENEWAL", "I can help you with policy renewal. Let me check your policy status...");

        // PAYMENT intent
        intentPatterns.put("PAYMENT", Arrays.asList(
                Pattern.compile(".*\\b(pay|payment|bill|invoice|due|premium|installment)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*how (much|to) pay.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*make (a )?payment.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*payment (method|option|history).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*when is (the )?payment due.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*outstanding (balance|amount).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("PAYMENT", "I can help you with payments. Would you like to make a payment now?");

        // EMERGENCY intent
        intentPatterns.put("EMERGENCY", Arrays.asList(
                Pattern.compile(".*\\b(emergency|urgent|immediate|right now|asap|help!|accident now)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*need help (right )?now.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*(car broke down|stuck on road|need tow).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*(ambulance|police|fire).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("EMERGENCY", " EMERGENCY ASSISTANCE\n\n" +
                "24/7 Emergency Hotline: +996 XXX XXX XXX\n\n" +
                "For immediate assistance:\n" +
                "• Roadside assistance: +996 XXX XXX XXX\n" +
                "• Ambulance: 103\n" +
                "• Police: 102\n" +
                "• Fire: 101\n\n" +
                "Please stay calm. Help is on the way.");

        // FAREWELL intent
        intentPatterns.put("FAREWELL", Arrays.asList(
                Pattern.compile(".*\\b(bye|goodbye|see you|talk to you later|have a good day)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile("^bye$|^goodbye$", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*\\b(thanks?|thank you|appreciate it)\\b.*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("FAREWELL", "You're welcome! Feel free to return if you have more questions. Have a great day! 🌟");

        // HELP intent
        intentPatterns.put("HELP", Arrays.asList(
                Pattern.compile(".*\\b(help|support|assist|can you|what can you do)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile("^help$|^support$", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*how (does this work|can you help).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("HELP", " *I can help you with:*\n\n" +
                " *Policy Information* - Check your policy details, coverage, and documents\n" +
                " *Claims* - File a new claim or check existing claim status\n" +
                " *Renewals* - Renew your policy or check expiration date\n" +
                " *Payments* - Make payments, check due dates, and payment history\n" +
                " *Emergency* - Get immediate assistance and emergency contacts\n" +
                "️ *General Questions* - Ask about insurance types, coverage options\n\n" +
                "What would you like help with today?");

        // POLICY_NUMBER intent (specific for when user provides policy number)
        intentPatterns.put("PROVIDE_POLICY_NUMBER", Arrays.asList(
                Pattern.compile(".*\\b(POL[-:]?)?(\\d{6,10})\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile("^\\d{6,10}$", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("PROVIDE_POLICY_NUMBER", "Thank you for providing your policy number. Let me fetch the details for you.");

        // QUOTE_REQUEST intent
        intentPatterns.put("QUOTE_REQUEST", Arrays.asList(
                Pattern.compile(".*\\b(quote|estimate|how much would it cost)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*get (a )?quote.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*insurance (price|cost|rate).*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("QUOTE_REQUEST", "I can help you get a quote! Let me ask you a few questions about your vehicle and driving history.");

        // DOCUMENT_REQUEST intent
        intentPatterns.put("DOCUMENT_REQUEST", Arrays.asList(
                Pattern.compile(".*\\b(document|paperwork|form|download|pdf)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*get (my )?policy (document|paper).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*download (my )?policy.*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("DOCUMENT_REQUEST", "I can help you download your policy documents. Please provide your policy number.");

        // CONTACT_AGENT intent
        intentPatterns.put("CONTACT_AGENT", Arrays.asList(
                Pattern.compile(".*\\b(agent|human|representative|talk to someone|speak to)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*can I talk to a (real )?person.*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("CONTACT_AGENT", "I'll connect you with a human agent. Please wait while I transfer you...");

        // VEHICLE_INFO intent
        intentPatterns.put("VEHICLE_INFO", Arrays.asList(
                Pattern.compile(".*\\b(vehicle|car|truck|motorcycle|bike|automobile)\\b.*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*info about my (car|vehicle).*", Pattern.CASE_INSENSITIVE),
                Pattern.compile(".*what vehicle is insured.*", Pattern.CASE_INSENSITIVE)
        ));
        intentResponses.put("VEHICLE_INFO", "I can tell you about your insured vehicle. Could you provide your policy number?");
    }

    private void initializeKeywords() {
        intentKeywords.put("GREETING", Arrays.asList("hello", "hi", "hey", "greetings", "morning", "afternoon"));
        intentKeywords.put("POLICY_INQUIRY", Arrays.asList("policy", "coverage", "insured", "premium", "deductible", "insurance"));
        intentKeywords.put("CLAIM_FILING", Arrays.asList("claim", "accident", "damage", "crash", "stolen", "theft", "report"));
        intentKeywords.put("RENEWAL", Arrays.asList("renew", "expire", "expiration", "extend", "continue"));
        intentKeywords.put("PAYMENT", Arrays.asList("pay", "payment", "bill", "invoice", "due", "premium"));
        intentKeywords.put("EMERGENCY", Arrays.asList("emergency", "urgent", "immediate", "asap", "help", "accident now"));
        intentKeywords.put("FAREWELL", Arrays.asList("bye", "goodbye", "thanks", "thank you", "appreciate"));
        intentKeywords.put("HELP", Arrays.asList("help", "support", "assist", "what can you do", "how to"));
        intentKeywords.put("QUOTE_REQUEST", Arrays.asList("quote", "estimate", "cost", "price", "rate"));
        intentKeywords.put("DOCUMENT_REQUEST", Arrays.asList("document", "download", "pdf", "form", "paperwork"));
        intentKeywords.put("CONTACT_AGENT", Arrays.asList("agent", "human", "representative", "person", "speak"));
        intentKeywords.put("VEHICLE_INFO", Arrays.asList("vehicle", "car", "truck", "motorcycle", "automobile"));
    }

    private void initializeEntities() {
        intentEntities.put("POLICY_INQUIRY", Arrays.asList("policy_number", "coverage_type", "premium_amount"));
        intentEntities.put("CLAIM_FILING", Arrays.asList("incident_date", "incident_location", "damage_type"));
        intentEntities.put("PAYMENT", Arrays.asList("amount", "payment_method", "due_date"));
        intentEntities.put("VEHICLE_INFO", Arrays.asList("make", "model", "year", "license_plate"));
    }

    public IntentClassification classify(String message) {
        if (message == null || message.trim().isEmpty()) {
            return new IntentClassification("UNKNOWN", 0.0,
                    "I didn't understand that. Can you rephrase?", new HashMap<>());
        }

        String normalizedMessage = message.toLowerCase().trim();

        // Check for policy number first (high priority)
        String extractedPolicyNumber = extractPolicyNumber(normalizedMessage);
        if (extractedPolicyNumber != null) {
            Map<String, String> entities = new HashMap<>();
            entities.put("policy_number", extractedPolicyNumber);
            return new IntentClassification("PROVIDE_POLICY_NUMBER", 0.98,
                    "Thank you for providing policy number: " + extractedPolicyNumber, entities);
        }

        String bestIntent = "UNKNOWN";
        double bestScore = 0.0;
        Map<String, String> extractedEntities = new HashMap<>();

        for (Map.Entry<String, List<Pattern>> entry : intentPatterns.entrySet()) {
            String intent = entry.getKey();
            List<Pattern> patterns = entry.getValue();

            for (Pattern pattern : patterns) {
                java.util.regex.Matcher matcher = pattern.matcher(normalizedMessage);
                if (matcher.matches()) {
                    double score = calculateConfidence(normalizedMessage, intent);

                    // Extract entities if this intent matches
                    Map<String, String> entities = extractEntities(normalizedMessage, intent);
                    extractedEntities.putAll(entities);

                    // Boost score if we found relevant entities
                    if (!entities.isEmpty()) {
                        score = Math.min(1.0, score + 0.1);
                    }

                    if (score > bestScore) {
                        bestScore = score;
                        bestIntent = intent;
                    }
                    break;
                }
            }
        }

        // If no pattern matched, try keyword matching as fallback
        if (bestIntent.equals("UNKNOWN") || bestScore < 0.3) {
            IntentClassification keywordMatch = matchByKeywords(normalizedMessage);
            if (keywordMatch.getConfidence() > bestScore) {
                bestIntent = keywordMatch.getIntent();
                bestScore = keywordMatch.getConfidence();
                extractedEntities = keywordMatch.getExtractedEntities();
            }
        }

        String response = intentResponses.getOrDefault(bestIntent,
                "I'm not sure I understand. Could you please rephrase or ask about:\n" +
                        "• Policies \n" +
                        "• Claims \n" +
                        "• Payments \n" +
                        "• Renewals \n" +
                        "• Emergency \n\n" +
                        "Or type 'help' to see all options.");

        return new IntentClassification(bestIntent, bestScore, response, extractedEntities);
    }

    private IntentClassification matchByKeywords(String message) {
        String bestIntent = "UNKNOWN";
        double bestScore = 0.0;
        Map<String, String> entities = new HashMap<>();

        for (Map.Entry<String, List<String>> entry : intentKeywords.entrySet()) {
            String intent = entry.getKey();
            List<String> keywords = entry.getValue();

            long matchCount = keywords.stream()
                    .filter(keyword -> message.contains(keyword.toLowerCase()))
                    .count();

            if (matchCount > 0) {
                double score = matchCount / (double) keywords.size();
                if (score > bestScore) {
                    bestScore = score;
                    bestIntent = intent;

                    // Try to extract entities
                    entities = extractEntities(message, intent);
                }
            }
        }

        return new IntentClassification(bestIntent, bestScore,
                intentResponses.getOrDefault(bestIntent, "How can I help you?"), entities);
    }

    private double calculateConfidence(String message, String intent) {
        List<String> keywords = intentKeywords.getOrDefault(intent, new ArrayList<>());
        if (keywords.isEmpty()) return 0.5;

        // Count keyword matches
        long matchCount = keywords.stream()
                .filter(keyword -> message.contains(keyword.toLowerCase()))
                .count();

        // Calculate base confidence
        double baseConfidence = matchCount / (double) keywords.size();

        // Boost confidence based on message length (longer messages with keywords are more likely intentional)
        double lengthBoost = Math.min(0.2, message.length() / 100.0);

        // Check for question patterns
        if (message.contains("?") || message.startsWith("what") ||
                message.startsWith("how") || message.startsWith("when") ||
                message.startsWith("where") || message.startsWith("why")) {
            baseConfidence += 0.1;
        }

        return Math.min(1.0, baseConfidence + lengthBoost);
    }

    private String extractPolicyNumber(String message) {
        // Pattern for policy numbers (6-10 digits, optionally with POL- prefix)
        java.util.regex.Pattern policyPattern =
                java.util.regex.Pattern.compile("\\b(POL[-:]?)?(\\d{6,10})\\b", Pattern.CASE_INSENSITIVE);
        java.util.regex.Matcher matcher = policyPattern.matcher(message);

        if (matcher.find()) {
            return matcher.group(2);
        }
        return null;
    }

    private Map<String, String> extractEntities(String message, String intent) {
        Map<String, String> entities = new HashMap<>();

        // Extract policy number for any intent
        String policyNumber = extractPolicyNumber(message);
        if (policyNumber != null) {
            entities.put("policy_number", policyNumber);
        }

        // Intent-specific entity extraction
        switch (intent) {
            case "PAYMENT":
                // Extract amount if present (e.g., "$500", "500 dollars", "500 KGS")
                java.util.regex.Pattern amountPattern =
                        java.util.regex.Pattern.compile("\\b(\\d+(\\.\\d{2})?)\\s*(USD|KGS|\\$)?\\b");
                java.util.regex.Matcher amountMatcher = amountPattern.matcher(message);
                if (amountMatcher.find()) {
                    entities.put("amount", amountMatcher.group(1));
                }
                break;

            case "CLAIM_FILING":
                // Extract date if present
                java.util.regex.Pattern datePattern =
                        java.util.regex.Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})\\b");
                java.util.regex.Matcher dateMatcher = datePattern.matcher(message);
                if (dateMatcher.find()) {
                    entities.put("incident_date", dateMatcher.group(1));
                }
                break;

            case "VEHICLE_INFO":
                // Extract car brand/model if present (simple implementation)
                List<String> commonBrands = Arrays.asList("toyota", "honda", "bmw", "mercedes", "audi", "ford");
                for (String brand : commonBrands) {
                    if (message.contains(brand)) {
                        entities.put("vehicle_brand", brand);
                        break;
                    }
                }
                break;
        }

        return entities;
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static class IntentClassification {
        private String intent;
        private double confidence;
        private String defaultResponse;
        private Map<String, String> extractedEntities;

        public boolean hasPolicyNumber() {
            return extractedEntities != null && extractedEntities.containsKey("policy_number");
        }

        public String getPolicyNumber() {
            return extractedEntities != null ? extractedEntities.get("policy_number") : null;
        }

        public boolean isHighConfidence() {
            return confidence >= 0.7;
        }

        public boolean isMediumConfidence() {
            return confidence >= 0.4 && confidence < 0.7;
        }

        public String getFormattedResponse() {
            StringBuilder response = new StringBuilder(defaultResponse);

            if (extractedEntities != null && !extractedEntities.isEmpty()) {
                response.append("\n\n *I noticed:*");
                for (Map.Entry<String, String> entry : extractedEntities.entrySet()) {
                    String key = entry.getKey().replace("_", " ");
                    response.append(String.format("\n• %s: %s", key, entry.getValue()));
                }
            }
            if (confidence < 0.5) {
                response.append("\n\n *Tip:* Try being more specific in your question.");
            }

            return response.toString();
        }
    }
}