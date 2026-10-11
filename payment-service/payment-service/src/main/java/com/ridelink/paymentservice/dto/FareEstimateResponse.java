package com.ridelink.paymentservice.dto;

public record FareEstimateResponse(
        Double distanceKm,
        Double estimatedFare
) {
}