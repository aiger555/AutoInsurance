package com.ain.insuranceservice.services;

import com.ain.insuranceservice.models.*;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;


@Service
@Data
public class InsuranceCalculationService {

    //basic rates
    private static final BigDecimal BASE_OSAGO = new BigDecimal("1680");
    private static final BigDecimal BASE_DSAGO = new BigDecimal("1900");
    private static final BigDecimal BASE_CASCO = new BigDecimal("14000");

    //koeff-s for passenger_car based on volume
    private static final BigDecimal ENGINE_0_2000 = new BigDecimal("1.0");
    private static final BigDecimal ENGINE_2001_3000 = new BigDecimal("1.205");
    private static final BigDecimal ENGINE_3001_PLUS = new BigDecimal("1.928");

    //koef-s for drivers
    private static final BigDecimal COFF_EXPERIENCED_DRIVERS = new BigDecimal("1.0");
    private static final BigDecimal COFF_YOUNG_DRIVERS = new BigDecimal("1.126");
    private static final BigDecimal COFF_UNLIMITED_DRIVERS = new BigDecimal("1.205");

    //koef-s for trucks based on max allowed weight
    private static final BigDecimal TRUCK_UP_TO_16T = new BigDecimal("1.607");
    private static final BigDecimal TRUCK_OVER_16T = new BigDecimal("1.928");

    //koef-s for other types of auto
    private static final BigDecimal MOTORCYCLE_COEFF = new BigDecimal("0.434");
    private static final BigDecimal BUS_UP_TO_16_SEATS = new BigDecimal("0.614");
    private static final BigDecimal BUS_OVER_16_SEATS = new BigDecimal("0.818");
    private static final BigDecimal ELECTRIC_UP_TO_150_KW = new BigDecimal("1.607");
    private static final BigDecimal ELECTRIC_OVER_150_KW = new BigDecimal("1.928");

    public BigDecimal calculatePremium(InsurancePolicy policy) {
        BigDecimal basePremium = getBasePremiumPolicyType(policy.getPolicyType());

        if (policy.getPolicyType() == PolicyType.CASCO) {
            return calculateCascoPremium(policy);
        } else {
            return calculateOsagoDsagoPremium(policy);
        }
    }
    private BigDecimal calculateOsagoDsagoPremium(InsurancePolicy policy) {
        BigDecimal basePremium = getBasePremiumPolicyType(policy.getPolicyType());
        Car car = policy.getInsuredCar();
        List<Driver> drivers = policy.getDrivers();
        BigDecimal vehicleCoefficient = calculateVehicleCoefficient(car);
        BigDecimal driverCoefficient = calculateDriverCoefficient(drivers, car);

        BigDecimal premium = basePremium
                .multiply(vehicleCoefficient)
                .multiply(driverCoefficient);

        return premium.setScale(2, BigDecimal.ROUND_HALF_UP);

    }

    private BigDecimal getBasePremiumPolicyType(PolicyType policyType) {
        return switch (policyType){
            case OSAGO -> BASE_OSAGO;
            case DSAGO -> BASE_DSAGO;
            case CASCO -> BASE_CASCO;
            default -> BASE_OSAGO;
        };
    }

    private BigDecimal calculateVehicleCoefficient(Car car) {
        VehicleType vehicleType = car.getVehicleType();

        return switch (vehicleType){
            case PASSENGER_CAR -> calculatePassengerCarCoefficient(car);
            case TRUCK -> calculateTruckCoefficient(car);
            case TRAILER, SEMI_TRAILER, SPECIAL_VEHICLE, MOTORCYCLE -> MOTORCYCLE_COEFF;
            case BUS, MINIBUS -> calculateBusCoefficient(car);
            case ELECTRIC_CAR -> calculateElectricCoefficient(car);
            default -> new BigDecimal("1.0");
        };
    }

    private BigDecimal calculatePassengerCarCoefficient(Car car) {
        Double engineVolume = car.getEngineVolume();
        if (engineVolume == null) return ENGINE_0_2000;

        if (engineVolume <= 2000) return ENGINE_0_2000;
        else if (engineVolume <= 3000) return ENGINE_2001_3000;
        else return ENGINE_3001_PLUS;
    }

    private BigDecimal calculateTruckCoefficient(Car car) {
        Double maxAllowedWeight = car.getMaxAllowedWeight();
        if (maxAllowedWeight == null) return TRUCK_UP_TO_16T;

        if (maxAllowedWeight <= 16000) return TRUCK_UP_TO_16T;
        else return TRUCK_OVER_16T;
    }

    private BigDecimal calculateBusCoefficient(Car car) {
        Double passengerCapacity = car.getPassengerCapacity();
        if (passengerCapacity == null || passengerCapacity <= 16) return BUS_OVER_16_SEATS;
        else return BUS_OVER_16_SEATS;
    }

    private BigDecimal calculateElectricCoefficient(Car car) {
        Double batteryCapacity = car.getBatteryCapacity();
        if (batteryCapacity == null) return ELECTRIC_UP_TO_150_KW;
        if (batteryCapacity <= 150) return ELECTRIC_UP_TO_150_KW;
        else return ELECTRIC_OVER_150_KW;
    }

    private BigDecimal calculateDriverCoefficient(List<Driver> drivers, Car car) {
        if (drivers == null || drivers.isEmpty()) return COFF_UNLIMITED_DRIVERS;

        boolean allExperienced = drivers.stream().allMatch(this::isExperiencedDriver);
        if (allExperienced) return COFF_EXPERIENCED_DRIVERS;
        else return COFF_YOUNG_DRIVERS;
    }

    private boolean isExperiencedDriver(Driver driver) {
        if (driver.getBirthDate() == null || driver.getDrivingExperience() == null) return false;

        int age = Period.between(driver.getBirthDate(), LocalDate.now()).getYears();
        if (age < 25) return false;

        int experienceYears = Period.between(driver.getDrivingExperience(), LocalDate.now()).getYears();
        return experienceYears >= 3;
    }

    public BigDecimal calculateCascoPremium(InsurancePolicy policy) {
        Car car = policy.getInsuredCar();
        String usagePurpose = "personal";
        BigDecimal marketValue = new BigDecimal("500000");
        BigDecimal franchise = new BigDecimal("5000");
        boolean isLegalEntity = false;

        return calculateCascoPremium(car, usagePurpose, marketValue, franchise, isLegalEntity);
    }

    public BigDecimal calculateCascoPremium(Car car, String usagePurpose, BigDecimal marketValue, BigDecimal franchise, boolean isLegalEntity) {
        BigDecimal usageCoefficient = "rent".equals(usagePurpose) ?
                new BigDecimal("1.3") : new BigDecimal("1.0");

        BigDecimal ageCoefficient = calculateCarAgeCoefficient(car.getManufactureYear());
        BigDecimal franchiseCoefficient = calculateFranchiseCoefficient(franchise);
        BigDecimal vehicleTypeCoefficient = calculateCascoVehicleCoefficient(car);
        BigDecimal baseCascoRate = new BigDecimal("0.05");

        BigDecimal premium = marketValue
                .multiply(vehicleTypeCoefficient)
                .multiply(ageCoefficient)
                .multiply(franchiseCoefficient)
                .multiply(usageCoefficient)
                .multiply(baseCascoRate);

        return premium.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateCascoVehicleCoefficient(Car car) {
        VehicleType vehicleType = car.getVehicleType();

        return switch (vehicleType){
            case PASSENGER_CAR, TRAILER, SEMI_TRAILER, MOTORCYCLE, SPECIAL_VEHICLE -> new BigDecimal("1.0");
            case TRUCK -> new BigDecimal("1.2");
            case BUS, MINIBUS -> new BigDecimal("1.1");
            case ELECTRIC_CAR -> new BigDecimal("1.3");
            default -> new BigDecimal("1.0");
        };
    }

    private BigDecimal calculateCarAgeCoefficient(int manufactureYear) {
        int carAge = LocalDate.now().getYear() - manufactureYear;
        if (carAge <= 3) return new BigDecimal("1.0");
        else if (carAge <= 5) return new BigDecimal("0.9");
        else if (carAge <= 7) return new BigDecimal("0.8");
        else return new BigDecimal("0.7");
    }

    private BigDecimal calculateFranchiseCoefficient(BigDecimal franchise) {
        if (franchise.compareTo(new BigDecimal("4375")) <= 0) return new BigDecimal("1.0");
        else if (franchise.compareTo(new BigDecimal("8750")) <= 0) return new BigDecimal("0.9");
        else return new BigDecimal("0.8");
    }


}
