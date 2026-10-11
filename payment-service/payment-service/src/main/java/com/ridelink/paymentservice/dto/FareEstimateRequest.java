package com.ridelink.paymentservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record FareEstimateRequest(

        @NotNull(message = "Distance is required")
        @DecimalMin(value = "0.1", message = "Distance must be greater than 0")
        Double distanceKm
) {
}