package com.ridelink.paymentservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class FareCalculatorTests {

	private final FareCalculator fareCalculator = new FareCalculator();

	@Test
	void calculatesBaseFareAndDistanceCharge() {
		BigDecimal fare = fareCalculator.calculateFare(
				new BigDecimal("12.5"), new BigDecimal("100.00"), new BigDecimal("40.00"));

		assertEquals(new BigDecimal("600.00"), fare);
	}

	@Test
	void roundsTheTotalFareToTwoDecimalPlaces() {
		BigDecimal fare = fareCalculator.calculateFare(
				new BigDecimal("1.25"), new BigDecimal("10.00"), new BigDecimal("2.10"));

		assertEquals(new BigDecimal("12.63"), fare);
	}

	@Test
	void rejectsNegativeDistanceOrRates() {
		assertThrows(IllegalArgumentException.class, () -> fareCalculator.calculateFare(
				new BigDecimal("-1"), BigDecimal.ZERO, BigDecimal.ONE));
		assertThrows(IllegalArgumentException.class, () -> fareCalculator.calculateFare(
				BigDecimal.ONE, BigDecimal.ZERO, new BigDecimal("-1")));
	}
}
