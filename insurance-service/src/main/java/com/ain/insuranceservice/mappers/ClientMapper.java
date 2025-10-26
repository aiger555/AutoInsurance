package com.ain.insuranceservice.mappers;

import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.models.Client;

public class ClientMapper {
    public static ClientResponseDTO toDTO(Client client) {
        ClientResponseDTO clientDTO = new ClientResponseDTO();
        clientDTO.setId(client.getId().toString());
        clientDTO.setFullname(client.getFullName());
        clientDTO.setDateOfBirth(client.getDateOfBirth().toString());
        clientDTO.setPhoneNumber(client.getPhoneNumber());
        clientDTO.setPassportNumber(client.getPassport_number());
        clientDTO.setAddress(client.getAddress());
        return clientDTO;
    }
}
