package com.ridelink.paymentservice.dto;

import com.ridelink.paymentservice.model.PaymentMethod;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessPaymentRequest {

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @NotBlank(message = "Driver ID is required")
    private String driverId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than 0")
    private Double amount;

    @NotNull(message = "Payment method is required (CARD, CASH, WALLET)")
    private PaymentMethod paymentMethod;

    private Boolean simulateFailure;
}
