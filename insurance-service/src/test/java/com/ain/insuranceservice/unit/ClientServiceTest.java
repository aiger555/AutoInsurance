package com.ain.insuranceservice.unit;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.exception.ClientNotFoundException;
import com.ain.insuranceservice.exception.PinAlreadyExistsException;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.repositories.ClientRepository;
import com.ain.insuranceservice.services.ClientService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @InjectMocks
    private ClientService clientService;

    private Client testClient;
    private ClientRequestDTO requestDTO;
    private UUID clientId;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        testClient = new Client();
        testClient.setId(clientId);
        testClient.setFullName("Ivan Ivanov");
        testClient.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testClient.setPhoneNumber("+996555123456");
        testClient.setPassport_number("AN1234567");
        testClient.setPin("12345678901234");
        testClient.setAddress("Bishkek, Chui 123");

        requestDTO = new ClientRequestDTO();
        requestDTO.setFullName("Ivan Ivanov");
        requestDTO.setDateOfBirth("1985-05-15");
        requestDTO.setPhoneNumber("+996555123456");
        requestDTO.setPassportNumber("AN1234567");
        requestDTO.setPin("12345678901234");
        requestDTO.setAddress("Bishkek, Chui 123");
    }

    @Test
    void createClient_ValidData_ReturnsClient() {
        when(clientRepository.existsByPin(requestDTO.getPin())).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        ClientResponseDTO result = clientService.createClient(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientId.toString());
        assertThat(result.getFullname()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void createClient_DuplicatePin_ThrowsException() {
        when(clientRepository.existsByPin(requestDTO.getPin())).thenReturn(true);

        assertThatThrownBy(() -> clientService.createClient(requestDTO))
                .isInstanceOf(PinAlreadyExistsException.class);
    }

    @Test
    void getClients_ReturnsAllClients() {
        List<Client> clients = Arrays.asList(testClient);
        when(clientRepository.findAll()).thenReturn(clients);

        List<ClientResponseDTO> result = clientService.getClients();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getFullname()).isEqualTo("Ivan Ivanov");
    }

    @Test
    void updateClient_ValidData_ReturnsUpdatedClient() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));
        when(clientRepository.existsByPinAndIdNot(requestDTO.getPin(), clientId)).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenReturn(testClient);

        ClientResponseDTO result = clientService.updateClient(clientId, requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(clientId.toString());
    }

    @Test
    void updateClient_ClientNotFound_ThrowsException() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> clientService.updateClient(clientId, requestDTO))
                .isInstanceOf(ClientNotFoundException.class);
    }

    @Test
    void deleteClient_ValidId_DeletesClient() {
        doNothing().when(clientRepository).deleteById(clientId);

        clientService.deleteClient(clientId);

        verify(clientRepository).deleteById(clientId);
    }
}