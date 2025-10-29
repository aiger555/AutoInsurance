package com.ain.insuranceservice.controllers;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.services.ClientService;
import jakarta.validation.Valid;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Data
@RestController
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    @GetMapping
    public ResponseEntity<List<ClientResponseDTO>> getClients() {
        List<ClientResponseDTO> clients = clientService.getClients();
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ClientResponseDTO> createClient(@Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        ClientResponseDTO clientResponseDTO = clientService.createClient(clientRequestDTO);
        return new ResponseEntity<>(clientResponseDTO, HttpStatus.CREATED);
    }
}
