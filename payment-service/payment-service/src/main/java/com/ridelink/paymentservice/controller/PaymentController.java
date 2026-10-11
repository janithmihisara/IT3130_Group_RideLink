package com.ridelink.paymentservice.controller;

import com.ridelink.paymentservice.dto.PaymentResponse;
import com.ridelink.paymentservice.dto.ProcessPaymentRequest;
import com.ridelink.paymentservice.dto.ReceiptResponse;
import com.ridelink.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payment Management", description = "Endpoints for Simulated Payment Processing, Transaction Status & Receipts")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/process")
    @Operation(summary = "Process simulated payment", description = "Records simulated payment for completed ride and generates receipt number.")
    public ResponseEntity<PaymentResponse> processPayment(@Valid @RequestBody ProcessPaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieves payment record details by payment ID.")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable("id") String id) {
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ride/{rideId}")
    @Operation(summary = "Get payment by ride ID", description = "Retrieves payment record associated with a specific ride ID.")
    public ResponseEntity<PaymentResponse> getPaymentByRideId(@PathVariable("rideId") String rideId) {
        PaymentResponse response = paymentService.getPaymentByRideId(rideId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/receipt/{receiptNumber}")
    @Operation(summary = "Get receipt by receipt number", description = "Generates and retrieves official receipt by receipt number.")
    public ResponseEntity<ReceiptResponse> getReceiptByReceiptNumber(@PathVariable("receiptNumber") String receiptNumber) {
        ReceiptResponse response = paymentService.getReceiptByReceiptNumber(receiptNumber);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all payments", description = "Retrieves list of all payment transactions.")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }
}
