package com.ridelink.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteRideRequest {

    @NotBlank(message = "Payment method is required (CARD, CASH, WALLET)")
    private String paymentMethod;

    private Boolean simulatePaymentFailure;
}
