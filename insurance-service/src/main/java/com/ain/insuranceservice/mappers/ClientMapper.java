package com.ain.insuranceservice.mappers;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.models.Client;

import java.time.LocalDate;

public class ClientMapper {
    public static ClientResponseDTO toDTO(Client client) {
        ClientResponseDTO clientDTO = new ClientResponseDTO();
        clientDTO.setId(client.getId().toString());
        clientDTO.setFullname(client.getFullName());
        clientDTO.setDateOfBirth(client.getDateOfBirth().toString());
        clientDTO.setPhoneNumber(client.getPhoneNumber());
        clientDTO.setPassportNumber(client.getPassport_number());
        clientDTO.setPin(client.getPin());
        clientDTO.setAddress(client.getAddress());
        return clientDTO;
    }

    public static Client toModel(ClientRequestDTO clientRequestDTO) {
        Client client = new Client();
        client.setFullName(clientRequestDTO.getFullName());
        client.setDateOfBirth(LocalDate.parse(clientRequestDTO.getDateOfBirth()));
        client.setPhoneNumber(clientRequestDTO.getPhoneNumber());
        client.setPassport_number(clientRequestDTO.getPassportNumber());
        client.setPin(clientRequestDTO.getPin());
        client.setAddress(clientRequestDTO.getAddress());
        return client;
    }
}
