package com.ridelink.rideservice.exception;

public class InvalidRideStatusTransitionException extends RuntimeException {
    public InvalidRideStatusTransitionException(String message) {
        super(message);
    }
}
