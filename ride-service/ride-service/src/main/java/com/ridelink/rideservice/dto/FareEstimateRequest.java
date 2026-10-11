package com.ridelink.rideservice.dto;

import java.math.BigDecimal;

public record FareEstimateRequest(
    Long passengerId,
    BigDecimal pickupLatitude,
    BigDecimal pickupLongitude,
    BigDecimal destinationLatitude,
    BigDecimal destinationLongitude
) {
}
