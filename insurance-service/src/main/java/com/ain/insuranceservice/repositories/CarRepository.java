package com.ain.insuranceservice.repositories;

import com.ain.insuranceservice.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CarRepository extends JpaRepository<Car, UUID> {
    boolean existsByVin(String vin);
    boolean existsByVinAndIdNot(String vin, UUID id);
}
