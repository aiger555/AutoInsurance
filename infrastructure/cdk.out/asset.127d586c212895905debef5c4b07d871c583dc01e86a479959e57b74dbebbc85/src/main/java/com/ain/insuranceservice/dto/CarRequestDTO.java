package com.ain.insuranceservice.dto;

import com.ain.insuranceservice.models.Client;
import com.ain.insuranceservice.models.VehicleType;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CarRequestDTO {
    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotBlank(message = "Year of manufacture is required")
    private String manufactureYear;

    @NotBlank(message = "VIN is required")
    private String vin;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotBlank(message = "Registration Authority is required")
    private String registrationAuthority;

    @NotBlank(message = "Registration Date is required")
    private String registrationDate;

    @NotBlank(message = "TechPassport Number is required")
    private String techPassportNumber;

    private String engineVolume;
    private String maxAllowedWeight;
    private String batteryCapacity;
    private String passengerCapacity;

    @NotNull(message = "Vehicle Type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Owner is required")
    private Client owner;

    public void validate(){
        switch (vehicleType){
            case PASSENGER_CAR:
                if (engineVolume == null || engineVolume.isEmpty()){
                    throw new ValidationException("Engine Volume is required");
                }
                break;

            case TRUCK:
            case TRAILER:
            case SEMI_TRAILER:
                if (maxAllowedWeight == null || maxAllowedWeight.isEmpty()){
                    throw new ValidationException("Max Allowed Weight is required for " + vehicleType.getDisplayName());
                }
                break;
            case ELECTRIC_CAR:
                if (batteryCapacity == null || batteryCapacity.isEmpty()){
                    throw new ValidationException("Battery Capacity is required");
                }
                break;
            case BUS:
            case MINIBUS:
                if (passengerCapacity == null || passengerCapacity.isEmpty()){
                    throw new ValidationException("Passenger Capacity is required for " + vehicleType.getDisplayName());
                }
                break;
            case MOTORCYCLE:
            case SPECIAL_VEHICLE:
                break;
        }
    }

}
