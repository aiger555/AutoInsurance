package com.ain.insuranceservice.exception;

public class InsurancePolicyNotFoundException extends RuntimeException {
    public InsurancePolicyNotFoundException(String message) {
        super(message);
    }
}
