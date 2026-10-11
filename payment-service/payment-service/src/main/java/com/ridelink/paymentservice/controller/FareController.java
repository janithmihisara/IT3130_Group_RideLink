package com.ridelink.paymentservice.controller;

import com.ridelink.paymentservice.dto.FareEstimateRequest;
import com.ridelink.paymentservice.dto.FareEstimateResponse;
import com.ridelink.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fares")
@Tag(name = "Fare Estimation", description = "Endpoints for Ride Fare Calculation Rules & Estimation")
public class FareController {

    private final PaymentService paymentService;

    public FareController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/estimate")
    @Operation(summary = "Request fare estimate", description = "Calculates estimated fare based on distance, duration, and vehicle type multiplier.")
    public ResponseEntity<FareEstimateResponse> getFareEstimate(@Valid @RequestBody FareEstimateRequest request) {
        FareEstimateResponse response = paymentService.calculateFareEstimate(request);
        return ResponseEntity.ok(response);
    }
}
