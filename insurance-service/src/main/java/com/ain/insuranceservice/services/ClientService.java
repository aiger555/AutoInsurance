package com.ain.insuranceservice.services;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.exception.ClientNotFoundException;
import com.ain.insuranceservice.exception.PinAlreadyExistsException;
import com.ain.insuranceservice.mappers.ClientMapper;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public ClientResponseDTO createClient(ClientRequestDTO clientRequestDTO) {
        if (clientRepository.existsByPin(clientRequestDTO.getPin())) {
            throw new PinAlreadyExistsException("A client with this pin already exists" + clientRequestDTO.getPin());
        }
        Client newClient = clientRepository.save(ClientMapper.toModel(clientRequestDTO));
        return ClientMapper.toDTO(newClient);

    }

    public ClientResponseDTO updateClient(UUID id, ClientRequestDTO clientRequestDTO) {
        Client client = clientRepository.findById(id).orElseThrow(
                () -> new ClientNotFoundException("Client not found with ID: " + id));

        if (clientRepository.existsByPinAndIdNot(clientRequestDTO.getPin(), id)) {
            throw new PinAlreadyExistsException("A client with this pin already exists" + clientRequestDTO.getPin());
        }

        client.setFullName(clientRequestDTO.getFullName());
        client.setDateOfBirth(LocalDate.parse(clientRequestDTO.getDateOfBirth()));
        client.setPhoneNumber(clientRequestDTO.getPhoneNumber());
        client.setPassport_number(clientRequestDTO.getPassportNumber());
        client.setPin(clientRequestDTO.getPin());
        client.setAddress(clientRequestDTO.getAddress());
        Client updatedClient = clientRepository.save(client);
        return ClientMapper.toDTO(updatedClient);

    }

    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }
}
