package com.ridelink.paymentservice.service;

import com.ridelink.paymentservice.dto.*;
import com.ridelink.paymentservice.entity.Payment;
import com.ridelink.paymentservice.entity.PaymentStatus;
import com.ridelink.paymentservice.exception.PaymentNotFoundException;
import com.ridelink.paymentservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private static final double BASE_FARE = 200.0;
    private static final double PER_KM_RATE = 100.0;

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public FareEstimateResponse estimateFare(FareEstimateRequest request) {

        double fare =
                BASE_FARE + (request.distanceKm() * PER_KM_RATE);

        return new FareEstimateResponse(
                request.distanceKm(),
                fare
        );
    }

    public PaymentResponse createPayment(PaymentRequest request) {

        Payment payment = Payment.builder()
                .rideId(request.rideId())
                .amount(request.amount())
                .paymentMethod(request.paymentMethod())
                .status(PaymentStatus.PAID)
                .paidAt(LocalDateTime.now())
                .build();

        return mapToResponse(paymentRepository.save(payment));
    }

    public PaymentResponse getPayment(Long id) {

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() ->
                        new PaymentNotFoundException(id));

        return mapToResponse(payment);
    }

    public PaymentResponse getPaymentByRide(Long rideId) {

        Payment payment = paymentRepository
                .findByRideId(rideId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Payment not found for ride: " + rideId
                        ));

        return mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {

        return new PaymentResponse(
                payment.getId(),
                payment.getRideId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaidAt()
        );
    }
}