package com.ain.insuranceservice.integration;

import com.ain.insuranceservice.dto.ClientRequestDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.repositories.ClientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ClientControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarRepository carRepository;

    private ClientRequestDTO validClient;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
        clientRepository.deleteAll();

        validClient = new ClientRequestDTO();
        validClient.setFullName("Test Client");
        validClient.setDateOfBirth("1990-01-01");
        validClient.setPhoneNumber("+996555000000");
        validClient.setPassportNumber("AN1234567");
        validClient.setPin("12345678901234");
        validClient.setAddress("Bishkek, Test Street 1");
    }

    @Test
    void createClient_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClient)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullname").value("Test Client"))
                .andExpect(jsonPath("$.pin").value("12345678901234"));
    }

    @Test
    void createClient_DuplicatePin_ReturnsConflict() throws Exception {
        Client existingClient = new Client();
        existingClient.setFullName("Existing Client");
        existingClient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        existingClient.setPhoneNumber("+996555111111");
        existingClient.setPassport_number("AN9999999");
        existingClient.setPin("12345678901234");
        existingClient.setAddress("Bishkek, Old Street 1");
        clientRepository.save(existingClient);

        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClient)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllClients_ReturnsListOfClients() throws Exception {
        Client client = new Client();
        client.setFullName("Test Client");
        client.setDateOfBirth(LocalDate.of(1990, 1, 1));
        client.setPhoneNumber("+996555000000");
        client.setPassport_number("AN1234567");
        client.setPin("12345678901234");
        client.setAddress("Bishkek, Test Street 1");
        clientRepository.save(client);

        mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getClientById_ValidId_ReturnsClient() throws Exception {
        Client client = new Client();
        client.setFullName("Test Client");
        client.setDateOfBirth(LocalDate.of(1990, 1, 1));
        client.setPhoneNumber("+996555000000");
        client.setPassport_number("AN1234567");
        client.setPin("12345678901234");
        client.setAddress("Bishkek, Test Street 1");
        Client saved = clientRepository.save(client);

        mockMvc.perform(get("/clients/{id}", saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value("Test Client"));
    }

    @Test
    void getClientById_InvalidId_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/clients/{id}", "00000000-0000-0000-0000-000000000000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateClient_ValidData_ReturnsUpdatedClient() throws Exception {
        Client client = new Client();
        client.setFullName("Original Name");
        client.setDateOfBirth(LocalDate.of(1990, 1, 1));
        client.setPhoneNumber("+996555000000");
        client.setPassport_number("AN1234567");
        client.setPin("12345678901234");
        client.setAddress("Bishkek, Test Street 1");
        Client saved = clientRepository.save(client);

        validClient.setFullName("Updated Name");

        mockMvc.perform(put("/clients/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validClient)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullname").value("Updated Name"));
    }

    @Test
    void deleteClient_ValidId_ReturnsNoContent() throws Exception {
        Client client = new Client();
        client.setFullName("Test Client");
        client.setDateOfBirth(LocalDate.of(1990, 1, 1));
        client.setPhoneNumber("+996555000000");
        client.setPassport_number("AN1234567");
        client.setPin("12345678901234");
        client.setAddress("Bishkek, Test Street 1");
        Client saved = clientRepository.save(client);

        mockMvc.perform(delete("/clients/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/clients/{id}", saved.getId()))
                .andExpect(status().isNotFound());
    }
}