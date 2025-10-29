package com.ain.insuranceservice.mappers;

import com.ain.insuranceservice.dto.CarRequestDTO;
import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.models.Car;
import com.ain.insuranceservice.models.Client;

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
        carDTO.setEngineVolume(car.getEngineVolume().toString());
        carDTO.setMaxAllowedWeight(car.getMaxAllowedWeight().toString());
        carDTO.setBatteryCapacity(car.getBatteryCapacity().toString());
        carDTO.setPassengerCapacity(car.getPassengerCapacity().toString());
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
        car.setEngineVolume(Double.valueOf(carRequestDTO.getEngineVolume()));
        car.setMaxAllowedWeight(Double.valueOf(carRequestDTO.getMaxAllowedWeight()));
        car.setBatteryCapacity(Double.valueOf(carRequestDTO.getBatteryCapacity()));
        car.setPassengerCapacity(Double.valueOf(carRequestDTO.getPassengerCapacity()));
        car.setVehicleType(carRequestDTO.getVehicleType());
        car.setOwner(carRequestDTO.getOwner());
        return car;
    }
}
