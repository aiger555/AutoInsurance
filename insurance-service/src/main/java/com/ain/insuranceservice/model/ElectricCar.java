package com.ain.insuranceservice.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@PrimaryKeyJoinColumn(name = "car_id")
public class ElectricCar extends Car {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    private Double batteryCapacity;
}
