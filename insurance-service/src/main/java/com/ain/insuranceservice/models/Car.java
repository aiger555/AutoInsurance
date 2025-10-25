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
    private int engineVolume;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Client owner;

    @Enumerated(EnumType.STRING)
    private VehicleType type;

}


