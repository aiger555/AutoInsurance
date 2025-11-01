package com.ain.insuranceservice.models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table (name = "insurance_policies")
public class InsurancePolicy {
    @Id
    private String policyNumber;

    @Enumerated(EnumType.STRING)
    private PolicyType policyType;

    private BigDecimal premium;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String policyHolder;

    @ManyToOne
    @JoinColumn(name = "vehicle_owner_id")
    private Client vehicleOwner;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car insuredCar;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL)
    private List<Driver> drivers = new ArrayList<>();

    private String comissarNumber;
    private String companyNumber;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
