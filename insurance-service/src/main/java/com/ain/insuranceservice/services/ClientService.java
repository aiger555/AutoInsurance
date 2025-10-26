package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.mappers.ClientMapper;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClientService {
    private ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<ClientResponseDTO> getClients() {
        List<Client> clients = clientRepository.findAll();
        return clients.stream()
                .map(ClientMapper::toDTO).toList();
    }
}
