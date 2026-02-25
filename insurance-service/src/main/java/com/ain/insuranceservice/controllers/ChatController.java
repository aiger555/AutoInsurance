package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.chat.ChatMessageDTO;
import com.ain.insuranceservice.dto.chat.ChatRequestDTO;
import com.ain.insuranceservice.dto.chat.ChatResponseDTO;
import com.ain.insuranceservice.services.chat.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
@Tag(name = "Chatbot", description = "Insurance Chatbot API")
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/message")
    @Operation(summary = "Send a message to chatbot")
    public ResponseEntity<ChatResponseDTO> sendMessage(@Valid @RequestBody ChatRequestDTO request) {
        log.info("Received chat message: {}", request.getMessage());
        ChatResponseDTO response = chatService.processMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/session/{sessionId}/history")
    @Operation(summary = "Get chat history")
    public ResponseEntity<List<ChatMessageDTO>> getChatHistory(@PathVariable String sessionId) {
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/session/{sessionId}")
    @Operation(summary = "End chat session")
    public ResponseEntity<Void> endSession(@PathVariable String sessionId) {
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/chat.send")
    public void sendMessageWebSocket(@Payload ChatRequestDTO request) {
        log.info("WebSocket message received: {}", request.getMessage());
        ChatResponseDTO response = chatService.processMessage(request);
        messagingTemplate.convertAndSend("/topic/chat/" + request.getSessionId(), response);
    }
}