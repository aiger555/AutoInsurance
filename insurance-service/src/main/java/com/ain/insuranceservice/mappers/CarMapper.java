package com.ain.insuranceservice.mappers;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.VehicleType;

import java.time.LocalDate;

public class CarMapper {
    public static CarResponseDTO toDTO(Car car) {
        CarResponseDTO carDTO = new CarResponseDTO();
        carDTO.setId(car.getId().toString());
        carDTO.setBrand(car.getBrand());
        carDTO.setModel(car.getModel());
        carDTO.setManufactureYear(String.valueOf(car.getManufactureYear()));
        carDTO.setVin(car.getVin());
        carDTO.setLicensePlate(car.getLicensePlate());
        carDTO.setRegistrationAuthority(car.getRegistrationAuthority());
        carDTO.setRegistrationDate(car.getRegistrationDate().toString());
        carDTO.setTechPassportNumber(car.getTechPassportNumber());
        if (car.getEngineVolume() != null) {
            carDTO.setEngineVolume(car.getEngineVolume().toString());
        }
        if (car.getMaxAllowedWeight() != null) {
            carDTO.setMaxAllowedWeight(car.getMaxAllowedWeight().toString());
        }
        if (car.getBatteryCapacity() != null) {
            carDTO.setBatteryCapacity(car.getBatteryCapacity().toString());
        }
        if (car.getPassengerCapacity() != null) {
            carDTO.setPassengerCapacity(car.getPassengerCapacity().toString());
        }
        carDTO.setVehicleType(car.getVehicleType());
        carDTO.setOwner(car.getOwner());

        return carDTO;
    }

    public static Car toModel(CarRequestDTO carRequestDTO) {
        Car car = new Car();
        car.setBrand(carRequestDTO.getBrand());
        car.setModel(carRequestDTO.getModel());
        car.setManufactureYear(Integer.parseInt(carRequestDTO.getManufactureYear()));
        car.setVin(carRequestDTO.getVin());
        car.setLicensePlate(carRequestDTO.getLicensePlate());
        car.setRegistrationAuthority(carRequestDTO.getRegistrationAuthority());
        car.setRegistrationDate(LocalDate.parse(carRequestDTO.getRegistrationDate()));
        car.setTechPassportNumber(carRequestDTO.getTechPassportNumber());
//        car.setEngineVolume(Double.valueOf(carRequestDTO.getEngineVolume()));
//        car.setMaxAllowedWeight(Double.valueOf(carRequestDTO.getMaxAllowedWeight()));
//        car.setBatteryCapacity(Double.valueOf(carRequestDTO.getBatteryCapacity()));
//        car.setPassengerCapacity(Double.valueOf(carRequestDTO.getPassengerCapacity()));
        car.setVehicleType(carRequestDTO.getVehicleType());
        car.setOwner(carRequestDTO.getOwner());

        VehicleType vehicleType = carRequestDTO.getVehicleType();
        if (vehicleType == VehicleType.PASSENGER_CAR) {
            if(carRequestDTO.getEngineVolume() != null && !carRequestDTO.getEngineVolume().isEmpty()) {
                car.setEngineVolume(Double.valueOf(carRequestDTO.getEngineVolume()));
            }else {
                throw new IllegalArgumentException("Engine volume is required");
            }
        } else if (vehicleType == VehicleType.TRUCK || vehicleType == VehicleType.TRAILER || vehicleType == VehicleType.SEMI_TRAILER) {
            if (carRequestDTO.getMaxAllowedWeight() != null && !carRequestDTO.getMaxAllowedWeight().isEmpty()) {
                car.setMaxAllowedWeight(Double.valueOf(carRequestDTO.getMaxAllowedWeight()));
            } else {
                throw new IllegalArgumentException("Max allowed weight is required");
            }
        } else if (vehicleType == VehicleType.ELECTRIC_CAR) {
            if (carRequestDTO.getBatteryCapacity() != null && !carRequestDTO.getBatteryCapacity().isEmpty()) {
                car.setBatteryCapacity(Double.valueOf(carRequestDTO.getBatteryCapacity()));
            } else {
                throw new IllegalArgumentException("Battery capacity is required");
            }
        } else if (vehicleType == VehicleType.BUS || vehicleType == VehicleType.TRUCK || vehicleType == VehicleType.MINIBUS) {
            if (carRequestDTO.getPassengerCapacity() != null && !carRequestDTO.getPassengerCapacity().isEmpty()) {
                car.setPassengerCapacity(Double.valueOf(carRequestDTO.getPassengerCapacity()));
            } else {
                throw new IllegalArgumentException("Passenger capacity is required");
            }
        }
        if (carRequestDTO.getEngineVolume() != null) {
            car.setEngineVolume(Double.valueOf(carRequestDTO.getEngineVolume()));
        }
        if (carRequestDTO.getMaxAllowedWeight() != null) {
            car.setMaxAllowedWeight(Double.valueOf(carRequestDTO.getMaxAllowedWeight()));
        }
        if (carRequestDTO.getBatteryCapacity() != null) {
            car.setBatteryCapacity(Double.valueOf(carRequestDTO.getBatteryCapacity()));
        }
        if (carRequestDTO.getPassengerCapacity() != null) {
            car.setPassengerCapacity(Double.valueOf(carRequestDTO.getPassengerCapacity()));
        }

        return car;
    }
}
