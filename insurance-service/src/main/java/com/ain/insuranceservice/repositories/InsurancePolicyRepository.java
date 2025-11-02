package com.ain.insuranceservice.repositories;

import com.ain.insuranceservice.models.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, String> {
    boolean existsByPolicyNumber(String policyNumber);

//    boolean existsByPolicyNumberAndPolicyNumberNot(String excludedPolicyNumber);
}
