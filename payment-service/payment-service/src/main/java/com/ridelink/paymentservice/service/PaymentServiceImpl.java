package com.ridelink.paymentservice.service;

import com.ridelink.paymentservice.dto.*;
import com.ridelink.paymentservice.exception.PaymentFailedException;
import com.ridelink.paymentservice.exception.ResourceNotFoundException;
import com.ridelink.paymentservice.model.*;
import com.ridelink.paymentservice.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final FareCalculator fareCalculator;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, FareCalculator fareCalculator) {
        this.paymentRepository = paymentRepository;
        this.fareCalculator = fareCalculator;
    }

    @Override
    public FareEstimateResponse calculateFareEstimate(FareEstimateRequest request) {
        BigDecimal baseFare = BigDecimal.valueOf(2.50);
        BigDecimal distanceRate = BigDecimal.valueOf(1.20);
        BigDecimal durationRate = BigDecimal.valueOf(0.30);
        BigDecimal distanceKm = BigDecimal.valueOf(request.getDistanceKm());
        BigDecimal distanceFare = distanceKm.multiply(distanceRate);
        BigDecimal durationFare = BigDecimal.valueOf(request.getEstimatedDurationMinutes()).multiply(durationRate);

        double multiplier = getVehicleMultiplier(request.getVehicleType());
        BigDecimal subtotal = fareCalculator.calculateFare(distanceKm, baseFare.add(durationFare), distanceRate);
        BigDecimal roundedFare = subtotal.multiply(BigDecimal.valueOf(multiplier)).setScale(2, RoundingMode.HALF_UP);

        String formula = String.format("(Base $%.2f + Distance $%.2f + Duration $%.2f) * Multiplier %.1fx",
                baseFare.doubleValue(), distanceFare.doubleValue(), durationFare.doubleValue(), multiplier);

        return FareEstimateResponse.builder()
                .estimatedFare(roundedFare.doubleValue())
                .baseFare(baseFare.doubleValue())
                .distanceFare(distanceFare.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .durationFare(durationFare.setScale(2, RoundingMode.HALF_UP).doubleValue())
                .vehicleTypeMultiplier(multiplier)
                .vehicleType(request.getVehicleType())
                .calculationFormula(formula)
                .build();
    }

    @Override
    public PaymentResponse processPayment(ProcessPaymentRequest request) {
        if (Boolean.TRUE.equals(request.getSimulateFailure())) {
            Payment failedPayment = Payment.builder()
                    .rideId(request.getRideId())
                    .passengerId(request.getPassengerId())
                    .driverId(request.getDriverId())
                    .amount(BigDecimal.valueOf(request.getAmount()))
                    .paymentMethod(request.getPaymentMethod())
                    .status(PaymentStatus.FAILED)
                    .transactionId("TXN-FAIL-" + UUID.randomUUID().toString().substring(0, 8))
                    .receiptNumber("RCPT-FAIL-" + System.currentTimeMillis())
                    .createdAt(LocalDateTime.now())
                    .build();

            paymentRepository.save(failedPayment);
            throw new PaymentFailedException("Simulated payment failure for rideId: " + request.getRideId() + ". Payment declined by issuer.");
        }

        String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String receiptNo = "RCPT-" + System.currentTimeMillis();

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .passengerId(request.getPassengerId())
                .driverId(request.getDriverId())
                .amount(BigDecimal.valueOf(request.getAmount()))
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.SUCCESS)
                .transactionId(txnId)
                .receiptNumber(receiptNo)
                .paidAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        return mapToPaymentResponse(saved);
    }

    @Override
    public PaymentResponse getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for id: " + id));
        return mapToPaymentResponse(payment);
    }

    @Override
    public PaymentResponse getPaymentByRideId(String rideId) {
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment record not found for rideId: " + rideId));
        return mapToPaymentResponse(payment);
    }

    @Override
    public ReceiptResponse getReceiptByReceiptNumber(String receiptNumber) {
        Payment payment = paymentRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for receipt number: " + receiptNumber));

        return ReceiptResponse.builder()
                .receiptNumber(payment.getReceiptNumber())
                .transactionId(payment.getTransactionId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .driverId(payment.getDriverId())
                .amount(payment.getAmount().doubleValue())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .paidAt(payment.getPaidAt())
                .companyName("RideLink Technologies Inc.")
                .build();
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        return paymentRepository.findAll().stream().map(this::mapToPaymentResponse).collect(Collectors.toList());
    }

    private double getVehicleMultiplier(VehicleType vehicleType) {
        if (vehicleType == null) return 1.0;
        return switch (vehicleType) {
            case BIKE -> 0.8;
            case TUKTUK -> 0.7;
            case SEDAN -> 1.0;
            case SUV -> 1.5;
        };
    }

    private PaymentResponse mapToPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .driverId(payment.getDriverId())
                .amount(payment.getAmount().doubleValue())
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionId(payment.getTransactionId())
                .receiptNumber(payment.getReceiptNumber())
                .paidAt(payment.getPaidAt())
                .build();
    }
}
