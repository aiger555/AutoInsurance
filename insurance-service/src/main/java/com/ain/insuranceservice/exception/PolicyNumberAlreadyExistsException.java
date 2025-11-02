package com.ain.insuranceservice.exception;

public class PolicyNumberAlreadyExistsException extends RuntimeException {
    public PolicyNumberAlreadyExistsException(String message) {
        super(message);
    }
}
