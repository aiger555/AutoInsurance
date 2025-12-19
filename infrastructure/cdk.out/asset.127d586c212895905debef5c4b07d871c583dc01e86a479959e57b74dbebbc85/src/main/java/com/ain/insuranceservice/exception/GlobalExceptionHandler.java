package com.ain.insuranceservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(
                error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PinAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handlePinAlreadyExistsException(PinAlreadyExistsException ex) {
        log.warn("Pin already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "PIN ALREADY EXISTS");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(VinAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleVinAlreadyExistsException(VinAlreadyExistsException ex) {
        log.warn("Vin already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "VIN ALREADY EXISTS");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LicenseNumberAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleLicenseNumberAlreadyExistsException(LicenseNumberAlreadyExistsException ex) {
        log.warn("License number already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "License number ALREADY EXISTS");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PolicyNumberAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handlePolicyNumberAlreadyExistsException(PolicyNumberAlreadyExistsException ex) {
        log.warn("Policy number already exists: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Policy number ALREADY EXISTS");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(ClientNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleClientNotFoundException(ClientNotFoundException ex) {
        log.warn("Client not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Client not found");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCarNotFoundException(CarNotFoundException ex) {
        log.warn("Car not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Car not found");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleDriverNotFoundException(DriverNotFoundException ex) {
        log.warn("Driver not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Driver not found");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InsurancePolicyNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleInsurancePolicyNotFoundException(InsurancePolicyNotFoundException ex) {
        log.warn("Insurance policy not found: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", "Insurance policy not found");
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
