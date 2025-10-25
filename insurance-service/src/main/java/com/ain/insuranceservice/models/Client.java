package com.ain.insuranceservice.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotNull
    private String fullName;

    @NotNull
    private LocalDate dateOfBirth;

    @NotNull
    private String phoneNumber;

    @NotNull
    @Column(unique = true)
    private String passport_number;

    @NotNull
    @Column(unique = true)
    private int PIN;

    @NotNull
    private String address;



}
