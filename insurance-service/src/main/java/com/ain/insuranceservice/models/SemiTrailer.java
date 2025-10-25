package com.ain.insuranceservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "car_id")
public class SemiTrailer extends Car {
//    @Id
//    @GeneratedValue(strategy = GenerationType.AUTO)
//    private UUID id;

    private Double maxAllowedWeight;
}
