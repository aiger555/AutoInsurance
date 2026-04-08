
package com.ain.insuranceservice.integration;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.VehicleType;
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
public class CarControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ClientRepository clientRepository;

    private CarRequestDTO validCar;
    private Client owner;

    @BeforeEach
    void setUp() {
        carRepository.deleteAll();
        clientRepository.deleteAll();

        owner = new Client();
        owner.setFullName("Car Owner");
        owner.setDateOfBirth(LocalDate.of(1985, 5, 15));
        owner.setPhoneNumber("+996555123456");
        owner.setPassport_number("AN1234567");
        owner.setPin("12345678901234");
        owner.setAddress("Bishkek, Chui 123");
        owner = clientRepository.save(owner);

        validCar = new CarRequestDTO();
        validCar.setBrand("Toyota");
        validCar.setModel("Camry");
        validCar.setManufactureYear("2020");
        validCar.setVin("JTDKB20U303000001");
        validCar.setLicensePlate("01KG123AB");
        validCar.setRegistrationAuthority("GVDD");
        validCar.setRegistrationDate("2020-05-15");
        validCar.setTechPassportNumber("TP123456");
        validCar.setEngineVolume("2.5");
        validCar.setVehicleType(VehicleType.PASSENGER_CAR);
        validCar.setOwner(owner);
    }

    @Test
    void createCar_ValidData_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Camry"));
    }

    @Test
    void createCar_DuplicateVin_ReturnsConflict() throws Exception {
        Car existingCar = new Car();
        existingCar.setBrand("Honda");
        existingCar.setModel("Accord");
        existingCar.setManufactureYear(2021);
        existingCar.setVin("JTDKB20U303000001");
        existingCar.setLicensePlate("02KG456CD");
        existingCar.setRegistrationAuthority("GVDD");
        existingCar.setRegistrationDate(LocalDate.of(2021, 6, 20));
        existingCar.setTechPassportNumber("TP987654");
        existingCar.setEngineVolume(2.0);
        existingCar.setVehicleType(VehicleType.PASSENGER_CAR);
        existingCar.setOwner(owner);
        carRepository.save(existingCar);

        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validCar)))
                .andExpect(status().isConflict());
    }

    @Test
    void getAllCars_ReturnsListOfCars() throws Exception {
        Car car = new Car();
        car.setBrand("Toyota");
        car.setModel("Camry");
        car.setManufactureYear(2020);
        car.setVin("JTDKB20U303000001");
        car.setLicensePlate("01KG123AB");
        car.setRegistrationAuthority("GVDD");
        car.setRegistrationDate(LocalDate.of(2020, 5, 15));
        car.setTechPassportNumber("TP123456");
        car.setEngineVolume(2.5);
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setOwner(owner);
        carRepository.save(car);

        mockMvc.perform(get("/cars"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}