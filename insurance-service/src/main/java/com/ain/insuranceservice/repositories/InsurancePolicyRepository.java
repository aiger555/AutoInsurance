package com.ain.insuranceservice.repositories;

import com.ain.insuranceservice.models.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, String> {
    boolean existsByPolicyNumber(String policyNumber);

//    boolean existsByPolicyNumberAndIdNot(String policyNumber, String id);

    @Query("SELECT p FROM InsurancePolicy p " +
            "LEFT JOIN FETCH p.vehicleOwner " +
            "LEFT JOIN FETCH p.insuredCar " +
            "LEFT JOIN FETCH p.drivers " +
            "WHERE p.policyNumber = :policyNumber")
    InsurancePolicy findByPolicyNumberWithAssociations(@Param("policyNumber") String policyNumber);
}
