package com.ain.insuranceservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String brand;

    @NotNull
    private String model;

    @NotNull
    private int manufactureYear;

    @NotNull
    @Column(unique = true)
    private String vin;

    @NotNull
    @Column(unique = true)
    private String licencePlate;

    @NotNull
    private String registrationAuthority;

    @NotNull
    private LocalDate registrationDate;

    @NotNull
    private String techPassportNumber;

    @NotNull
    private Double engineVolume;
    private Double maxAllowedWeight; // for trucks, trailers, semi-trailers
    private Double batteryCapacity; // for electrocars
    private Double passengerCapacity; // for buses, minibuses


    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Client owner;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    public boolean isPassengerCar(){
        return vehicleType == VehicleType.PASSENGER_CAR;
    }

    public boolean isTruck(){
        return vehicleType == VehicleType.TRUCK;
    }

    public boolean isElectricCar(){
        return vehicleType == VehicleType.ELECTRIC_CAR;
    }

    public boolean isMotorcycle(){
        return vehicleType == VehicleType.MOTORCYCLE;
    }

    public boolean isTrailer(){
        return vehicleType == VehicleType.TRAILER;
    }

    public boolean isSemiTrailer(){
        return vehicleType == VehicleType.SEMI_TRAILER;
    }

    public boolean isBus(){
        return vehicleType == VehicleType.BUS;
    }

    public boolean isMinibus(){
        return vehicleType == VehicleType.MINIBUS;
    }

    public boolean isSpecialVehicle(){
        return vehicleType == VehicleType.SPECIAL_VEHICLE;
    }









}


