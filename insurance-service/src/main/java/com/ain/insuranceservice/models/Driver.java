package com.ain.insuranceservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "policy_id")
    private InsurancePolicy policy;

    private String fullName;
    private LocalDate birthDate;
    private String licenceNumber;
    private LocalDate drivingExperience;
}
