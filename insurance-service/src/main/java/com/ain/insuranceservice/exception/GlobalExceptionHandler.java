package com.ain.insuranceservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VinAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleVinAlreadyExists(VinAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "VIN ALREADY EXISTS");
        return error;
    }

    @ExceptionHandler(PolicyNumberAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlePolicyNumberAlreadyExists(PolicyNumberAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Policy number ALREADY EXISTS");
        return error;
    }

    @ExceptionHandler(PinAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handlePinAlreadyExists(PinAlreadyExistsException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "PIN ALREADY EXISTS");
        return error;
    }

    @ExceptionHandler(ClientNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleClientNotFound(ClientNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(CarNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleCarNotFound(CarNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return error;
    }

    @ExceptionHandler(InsurancePolicyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handlePolicyNotFound(InsurancePolicyNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Insurance policy not found");
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleGeneralException(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", "Internal server error: " + ex.getMessage());
        return error;
    }
}