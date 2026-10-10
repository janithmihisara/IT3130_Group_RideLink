package com.ridelink.paymentservice;

import com.ridelink.paymentservice.dto.*;
import com.ridelink.paymentservice.exception.PaymentFailedException;
import com.ridelink.paymentservice.exception.ResourceNotFoundException;
import com.ridelink.paymentservice.model.*;
import com.ridelink.paymentservice.repository.PaymentRepository;
import com.ridelink.paymentservice.service.FareCalculator;
import com.ridelink.paymentservice.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Spy
    private FareCalculator fareCalculator = new FareCalculator();

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        samplePayment = Payment.builder()
                .id("pay-123")
                .rideId("ride-456")
                .passengerId("pass-789")
                .driverId("drv-012")
                .amount(BigDecimal.valueOf(15.50))
                .paymentMethod(PaymentMethod.CARD)
                .status(PaymentStatus.SUCCESS)
                .transactionId("TXN-123456")
                .receiptNumber("RCPT-789012")
                .paidAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCalculateFareEstimate_Sedan() {
        FareEstimateRequest request = FareEstimateRequest.builder()
                .distanceKm(10.0)
                .estimatedDurationMinutes(20.0)
                .vehicleType(VehicleType.SEDAN)
                .build();

        FareEstimateResponse response = paymentService.calculateFareEstimate(request);

        assertNotNull(response);
        // Base 2.50 + Dist (10*1.20=12) + Dur (20*0.30=6) = 20.50 * 1.0 = 20.50
        assertEquals(20.50, response.getEstimatedFare());
        assertEquals(1.0, response.getVehicleTypeMultiplier());
    }

    @Test
    void testCalculateFareEstimate_SUV() {
        FareEstimateRequest request = FareEstimateRequest.builder()
                .distanceKm(10.0)
                .estimatedDurationMinutes(20.0)
                .vehicleType(VehicleType.SUV)
                .build();

        FareEstimateResponse response = paymentService.calculateFareEstimate(request);

        assertNotNull(response);
        // (2.50 + 12 + 6) * 1.5 = 30.75
        assertEquals(30.75, response.getEstimatedFare());
    }

    @Test
    void testProcessPayment_Success() {
        when(paymentRepository.save(any(Payment.class))).thenReturn(samplePayment);

        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .rideId("ride-456")
                .passengerId("pass-789")
                .driverId("drv-012")
                .amount(15.50)
                .paymentMethod(PaymentMethod.CARD)
                .build();

        PaymentResponse response = paymentService.processPayment(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.getStatus());
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessPayment_SimulatedFailure() {
        ProcessPaymentRequest request = ProcessPaymentRequest.builder()
                .rideId("ride-456")
                .passengerId("pass-789")
                .driverId("drv-012")
                .amount(15.50)
                .paymentMethod(PaymentMethod.CARD)
                .simulateFailure(true)
                .build();

        assertThrows(PaymentFailedException.class, () -> paymentService.processPayment(request));
    }

    @Test
    void testGetReceiptByReceiptNumber_NotFound() {
        when(paymentRepository.findByReceiptNumber("NON-EXISTENT")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> paymentService.getReceiptByReceiptNumber("NON-EXISTENT"));
    }
}
