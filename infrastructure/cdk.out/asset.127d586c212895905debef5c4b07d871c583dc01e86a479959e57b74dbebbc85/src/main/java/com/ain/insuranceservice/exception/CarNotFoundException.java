package com.ain.insuranceservice.exception;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(String message) {
        super(message);
    }
}
