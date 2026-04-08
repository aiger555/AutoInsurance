
package com.ain.insuranceservice.unit;

import com.ain.insuranceservice.dto.DriverRequestDTO;
import com.ain.insuranceservice.models.Driver;
import com.ain.insuranceservice.models.InsurancePolicy;
import com.ain.insuranceservice.repositories.DriverRepository;
import com.ain.insuranceservice.repositories.InsurancePolicyRepository;
import com.ain.insuranceservice.services.DriverService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @Mock
    private InsurancePolicyRepository policyRepository;

    @InjectMocks
    private DriverService driverService;

    private Driver testDriver;
    private DriverRequestDTO requestDTO;
    private InsurancePolicy testPolicy;
    private UUID driverId;

    @BeforeEach
    void setUp() {
        driverId = UUID.randomUUID();

        testPolicy = new InsurancePolicy();
        testPolicy.setPolicyNumber("POL001");

        testDriver = new Driver();
        testDriver.setId(driverId);
        testDriver.setFullName("Petr Petrov");
        testDriver.setBirthDate(LocalDate.of(1990, 5, 15));
        testDriver.setLicenseNumber("BG123456");
        testDriver.setDrivingExperience(LocalDate.of(2010, 6, 20));
        testDriver.setPolicy(testPolicy);

        requestDTO = new DriverRequestDTO();
        requestDTO.setPolicyNumber("POL001");
        requestDTO.setFullName("Petr Petrov");
        requestDTO.setBirthDate("1990-05-15");
        requestDTO.setLicenseNumber("BG123456");
        requestDTO.setDrivingExperience("2010-06-20");
    }

    @Test
    void createDriver_ValidData_ReturnsDriver() {
        when(policyRepository.findById("POL001")).thenReturn(Optional.of(testPolicy));
        when(driverRepository.existsByLicenseNumber(requestDTO.getLicenseNumber())).thenReturn(false);
        when(driverRepository.save(any(Driver.class))).thenReturn(testDriver);

        var result = driverService.createDriver(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result.getFullName()).isEqualTo("Petr Petrov");
    }
}