package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.VehicleType;
import lombok.Data;

@Data
public class CarResponseDTO {
    private String id;
    private String brand;
    private String model;
    private String manufactureYear;
    private String vin;
    private String licensePlate;
    private String registrationAuthority;
    private String registrationDate;
    private String techPassportNumber;
    private String engineVolume;
    private String maxAllowedWeight;
    private String batteryCapacity;
    private String passengerCapacity;
    private VehicleType vehicleType;
    private Client owner;
}
