package com.ridelink.driverservice.exception;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(Long id) {
        super("Driver not found with ID: " + id);
    }
}