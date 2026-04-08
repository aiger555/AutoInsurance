package com.ain.insuranceservice.unit;

import com.ain.insuranceservice.models.*;
import com.ain.insuranceservice.services.InsuranceCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
public class InsuranceCalculationServiceTest {

    private InsuranceCalculationService calculationService;

    @BeforeEach
    void setUp() {
        calculationService = new InsuranceCalculationService();
    }

    @Test
    void calculateOsagoPremium_PassengerCarSmallEngine_ReturnsCorrectValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(1500.0);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(createExperiencedDriver());

        BigDecimal result = calculationService.calculatePremium(policy);

        assertThat(result).isEqualByComparingTo(new BigDecimal("1680.00"));
    }

    @Test
    void calculateOsagoPremium_PassengerCarMediumEngine_ReturnsCorrectValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(2500.0);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(createExperiencedDriver());

        BigDecimal result = calculationService.calculatePremium(policy);

        assertThat(result).isEqualByComparingTo(new BigDecimal("2024.40"));
    }

    @Test
    void calculateOsagoPremium_PassengerCarLargeEngine_ReturnsCorrectValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(3500.0);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(createExperiencedDriver());

        BigDecimal result = calculationService.calculatePremium(policy);

        assertThat(result).isEqualByComparingTo(new BigDecimal("3239.04"));
    }

    @Test
    void calculateOsagoPremium_Motorcycle_ReturnsCorrectValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.MOTORCYCLE);

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(createExperiencedDriver());

        BigDecimal result = calculationService.calculatePremium(policy);

        assertThat(result).isEqualByComparingTo(new BigDecimal("729.12"));
    }

    @Test
    void calculateOsagoPremium_YoungDriver_ReturnsHigherPremium() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setEngineVolume(2000.0);

        Driver youngDriver = new Driver();
        youngDriver.setBirthDate(LocalDate.now().minusYears(20));
        youngDriver.setDrivingExperience(LocalDate.now().minusYears(1));

        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyType(PolicyType.OSAGO);
        policy.setInsuredCar(car);
        policy.setDrivers(Collections.singletonList(youngDriver));

        BigDecimal result = calculationService.calculatePremium(policy);

        assertThat(result).isEqualByComparingTo(new BigDecimal("1891.68"));
    }

    @Test
    void calculateCascoPremium_BasicPersonalUse_ReturnsCorrectValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setManufactureYear(2022);

        BigDecimal result = calculationService.calculateCascoPremium(
                car, "personal", new BigDecimal("500000"), new BigDecimal("4375"), false);

        assertThat(result).isEqualByComparingTo(new BigDecimal("22500.00"));
    }

    @Test
    void calculateCascoPremium_RentalUse_ReturnsHigherValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setManufactureYear(2022);

        BigDecimal result = calculationService.calculateCascoPremium(
                car, "rent", new BigDecimal("500000"), new BigDecimal("4375"), false);

        assertThat(result).isEqualByComparingTo(new BigDecimal("29250.00"));
    }

    @Test
    void calculateCascoPremium_ElectricCar_ReturnsHigherValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.ELECTRIC_CAR);
        car.setManufactureYear(2022);

        BigDecimal result = calculationService.calculateCascoPremium(
                car, "personal", new BigDecimal("500000"), new BigDecimal("4375"), false);

        assertThat(result).isEqualByComparingTo(new BigDecimal("29250.00"));
    }

    @Test
    void calculateCascoPremium_OldCar_ReturnsLowerValue() {
        Car car = new Car();
        car.setVehicleType(VehicleType.PASSENGER_CAR);
        car.setManufactureYear(2015);

        BigDecimal result = calculationService.calculateCascoPremium(
                car, "personal", new BigDecimal("500000"), new BigDecimal("4375"), false);

        assertThat(result).isEqualByComparingTo(new BigDecimal("17500.00"));
    }
    private java.util.List<Driver> createExperiencedDriver() {
        Driver driver = new Driver();
        driver.setBirthDate(LocalDate.now().minusYears(30));
        driver.setDrivingExperience(LocalDate.now().minusYears(10));
        return Collections.singletonList(driver);
    }
}