package com.ridelink.rideservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateRideRequest(
    @NotNull(message = "Passenger ID is required") Long passengerId,
    @NotBlank(message = "Pickup address is required") String pickupAddress,
    @NotBlank(message = "Destination address is required") String destinationAddress,
    @NotNull(message = "Pickup latitude is required") BigDecimal pickupLatitude,
    @NotNull(message = "Pickup longitude is required") BigDecimal pickupLongitude,
    @NotNull(message = "Destination latitude is required") BigDecimal destinationLatitude,
    @NotNull(message = "Destination longitude is required") BigDecimal destinationLongitude
) {
}
