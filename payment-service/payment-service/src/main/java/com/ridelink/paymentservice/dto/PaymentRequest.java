package com.ridelink.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest(

        @NotNull(message = "Ride ID is required")
        Long rideId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than 0")
        Double amount,

        @NotBlank(message = "Payment method is required")
        String paymentMethod
) {
}