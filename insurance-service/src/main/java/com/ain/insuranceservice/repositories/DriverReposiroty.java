package com.ain.insuranceservice.repositories;

import com.ain.insuranceservice.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DriverReposiroty extends JpaRepository<Driver, UUID> {
}
