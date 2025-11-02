package com.ain.insuranceservice.exception;

public class VinAlreadyExistsException extends RuntimeException {
    public VinAlreadyExistsException(String message) {
        super(message);
    }
}
