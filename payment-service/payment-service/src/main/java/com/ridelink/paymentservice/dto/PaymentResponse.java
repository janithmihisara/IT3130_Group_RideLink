package com.ridelink.paymentservice.dto;

import com.ridelink.paymentservice.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long rideId,
        Double amount,
        String paymentMethod,
        PaymentStatus status,
        LocalDateTime paidAt
) {
}