package com.ridelink.paymentservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import org.springframework.stereotype.Service;

@Service
public class FareCalculator {

	public BigDecimal calculateFare(BigDecimal distanceKm, BigDecimal baseFare, BigDecimal ratePerKm) {
		Objects.requireNonNull(distanceKm, "distanceKm must not be null");
		Objects.requireNonNull(baseFare, "baseFare must not be null");
		Objects.requireNonNull(ratePerKm, "ratePerKm must not be null");

		if (distanceKm.signum() < 0 || baseFare.signum() < 0 || ratePerKm.signum() < 0) {
			throw new IllegalArgumentException("Distance and fare rates must not be negative");
		}

		return baseFare.add(distanceKm.multiply(ratePerKm)).setScale(2, RoundingMode.HALF_UP);
	}
}
