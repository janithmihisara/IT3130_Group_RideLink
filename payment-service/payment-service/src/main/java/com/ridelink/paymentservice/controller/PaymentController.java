package com.ridelink.paymentservice.controller;

import com.ridelink.paymentservice.dto.*;
import com.ridelink.paymentservice.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/fares/estimate")
    public ResponseEntity<FareEstimateResponse> estimateFare(
            @Valid @RequestBody FareEstimateRequest request) {

        return ResponseEntity.ok(
                paymentService.estimateFare(request)
        );
    }

    @PostMapping("/api/payments")
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    @GetMapping("/api/payments/{id}")
    public ResponseEntity<PaymentResponse> getPayment(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                paymentService.getPayment(id)
        );
    }

    @GetMapping("/api/payments/ride/{rideId}")
    public ResponseEntity<PaymentResponse> getPaymentByRide(
            @PathVariable Long rideId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByRide(rideId)
        );
    }
}