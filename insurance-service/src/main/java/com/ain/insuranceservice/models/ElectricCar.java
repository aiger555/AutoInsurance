package com.ain.insuranceservice.models;

import jakarta.persistence.*;

@Entity
@PrimaryKeyJoinColumn(name = "car_id")
public class ElectricCar extends Car {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    private Double batteryCapacity;
}
