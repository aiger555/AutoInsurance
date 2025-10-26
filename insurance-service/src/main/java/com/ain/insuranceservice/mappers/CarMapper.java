package com.ain.insuranceservice.mappers;

import com.ain.insuranceservice.dto.CarResponseDTO;
import com.ain.insuranceservice.dto.ClientResponseDTO;
import com.ain.insuranceservice.models.Car;

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
}
