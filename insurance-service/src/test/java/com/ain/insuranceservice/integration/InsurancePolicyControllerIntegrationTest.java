package com.ain.insuranceservice.integration;

import com.ain.insuranceservice.dto.InsurancePolicyRequestDTO;
import com.ain.insuranceservice.models.*;
import com.ain.insuranceservice.repositories.CarRepository;
import com.ain.insuranceservice.repositories.ClientRepository;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
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
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class InsurancePolicyControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InsurancePolicyRepository policyRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CarRepository carRepository;

    private InsurancePolicyRequestDTO policyRequest;
    private Client testClient;
    private Car testCar;

    @BeforeEach
    void setUp() {
        policyRepository.deleteAll();
        carRepository.deleteAll();
        clientRepository.deleteAll();

        testClient = new Client();
        testClient.setFullName("Policy Owner");
        testClient.setDateOfBirth(LocalDate.of(1985, 5, 15));
        testClient.setPhoneNumber("+996555123456");
        testClient.setPassport_number("AN1234567");
        testClient.setPin("12345678901234");
        testClient.setAddress("Bishkek, Chui 123");
        testClient = clientRepository.save(testClient);

        testCar = new Car();
        testCar.setBrand("Toyota");
        testCar.setModel("Camry");
        testCar.setManufactureYear(2020);
        testCar.setVin("JTDKB20U303000001");
        testCar.setLicensePlate("01KG123AB");
        testCar.setRegistrationAuthority("GVDD");
        testCar.setRegistrationDate(LocalDate.of(2020, 5, 15));
        testCar.setTechPassportNumber("TP123456");
        testCar.setEngineVolume(2.5);
        testCar.setVehicleType(VehicleType.PASSENGER_CAR);
        testCar.setOwner(testClient);
        testCar = carRepository.save(testCar);

        policyRequest = new InsurancePolicyRequestDTO();
        policyRequest.setPolicyNumber("POL001");
        policyRequest.setPolicyType(PolicyType.OSAGO);
        policyRequest.setPolicyHolder(testClient.getFullName());
        policyRequest.setStartDate("2024-01-01");
        policyRequest.setEndDate("2024-12-31");
        policyRequest.setStatus("ACTIVE");
        policyRequest.setVehicleOwner(testClient);
        policyRequest.setInsuredCar(testCar);
        policyRequest.setDrivers(new ArrayList<>());
        policyRequest.setComissarNumber("+996555111111");
        policyRequest.setCompanyNumber("+996312111111");
    }

    @Test
    void createPolicy_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.policyNumber").value("POL001"))
                .andExpect(jsonPath("$.policyType").value("OSAGO"));
    }

    @Test
    void createPolicy_DuplicatePolicyNumber_ReturnsConflict() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllPolicies_ReturnsListOfPolicies() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/policies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getPolicyByNumber_ValidNumber_ReturnsPolicy() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/policies/{policyNumber}", "POL001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.policyNumber").value("POL001"));
    }

    @Test
    void getPolicyByNumber_InvalidNumber_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/policies/{policyNumber}", "INVALID"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePolicy_ValidData_ReturnsUpdatedPolicy() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated());

        policyRequest.setStatus("EXPIRED");

        mockMvc.perform(put("/policies/{policyNumber}", "POL001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EXPIRED"));
    }

    @Test
    void deletePolicy_ValidNumber_ReturnsNoContent() throws Exception {
        mockMvc.perform(post("/policies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(policyRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/policies/{policyNumber}", "POL001"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/policies/{policyNumber}", "POL001"))
                .andExpect(status().isNotFound());
    }
}