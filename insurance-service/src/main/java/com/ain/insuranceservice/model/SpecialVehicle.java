package com.ain.insuranceservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "car_id")
public class SpecialVehicle extends Car {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    private Double maxAllowedWeight;
}