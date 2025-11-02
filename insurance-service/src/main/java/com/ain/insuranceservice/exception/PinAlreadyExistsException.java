package com.ain.insuranceservice.exception;

public class PinAlreadyExistsException extends RuntimeException {
    public PinAlreadyExistsException(String message) {
        super(message);
    }
}
