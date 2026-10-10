package com.ridelink.paymentservice.service;

import com.ridelink.paymentservice.dto.*;

import java.util.List;

public interface PaymentService {
    FareEstimateResponse calculateFareEstimate(FareEstimateRequest request);
    PaymentResponse processPayment(ProcessPaymentRequest request);
    PaymentResponse getPaymentById(String id);
    PaymentResponse getPaymentByRideId(String rideId);
    ReceiptResponse getReceiptByReceiptNumber(String receiptNumber);
    List<PaymentResponse> getAllPayments();
}
