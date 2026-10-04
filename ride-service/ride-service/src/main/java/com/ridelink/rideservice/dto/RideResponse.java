package com.ridelink.rideservice.dto;

import com.ridelink.rideservice.entity.RideStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponse(
    Long id,
    Long passengerId,
    Long driverId,
    String pickupAddress,
    String destinationAddress,
    RideStatus status,
    BigDecimal estimatedFare,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
