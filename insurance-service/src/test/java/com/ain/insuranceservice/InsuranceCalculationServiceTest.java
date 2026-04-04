package com.ain.insuranceservice;

import com.ain.insuranceservice.models.*;
import com.ain.insuranceservice.services.InsuranceCalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InsuranceCalculationServiceTest {

    private final InsuranceCalculationService service = new InsuranceCalculationService();

    @Test
    @DisplayName("OSAGO Premium - Passenger Car with 2.5L Engine")
    void testCalculateOsagoPremium_PassengerCar_2500cc() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(2500.0);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(List.of());

        BigDecimal premium = service.calculatePremium(policy);

        // Base: 1680 × 1.205 (engine coefficient) × 1.205 (unlimited drivers) = 2438.57
        assertEquals(new BigDecimal("2438.57"), premium);
    }

    @Test
    @DisplayName("OSAGO Premium - Passenger Car with 1.6L Engine, Experienced Driver")
    void testCalculateOsagoPremium_ExperiencedDriver_1600cc() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(1600.0);

        Driver driver = new Driver();
        driver.setBirthDate(LocalDate.of(1980, 1, 1));  // Age > 25
        driver.setDrivingExperience(LocalDate.of(2005, 1, 1));  // Experience > 3 years

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(List.of(driver));

        BigDecimal premium = service.calculatePremium(policy);

        // Base: 1680 × 1.0 (engine coefficient) × 1.0 (experienced driver) = 1680.00
        assertEquals(new BigDecimal("1680.00"), premium);
    }

    @Test
    @DisplayName("CASCO Premium - New Car, Personal Use")
    void testCalculateCascoPremium_NewCar_PersonalUse() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setManufactureYear(2025);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.CASCO);
        policy.setInsuredCar(car);
        policy.setUsagePurpose("personal");
        policy.setMarketValue(new BigDecimal("1000000"));
        policy.setFranchise(new BigDecimal("5000"));

        BigDecimal premium = service.calculatePremium(policy);

        // Market value: 1,000,000 × 1.0 × 1.0 × 1.0 × 0.05 = 50,000
        assertEquals(new BigDecimal("50000.00"), premium);
    }

    @ParameterizedTest
    @DisplayName("Car Age Coefficient Test")
    @CsvSource({
            "2023, 1.0",
            "2021, 0.9",
            "2019, 0.8",
            "2017, 0.7"
    })
    void testCarAgeCoefficient(int manufactureYear, BigDecimal expectedCoeff) {
        BigDecimal coeff = service.calculateCarAgeCoefficient(manufactureYear);

        assertEquals(expectedCoeff, coeff);
    }
}

//Unit test