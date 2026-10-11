package com.ridelink.paymentservice.dto;

import com.ridelink.paymentservice.model.PaymentMethod;
import com.ridelink.paymentservice.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private String id;
    private String rideId;
    private String passengerId;
    private String driverId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionId;
    private String receiptNumber;
    private LocalDateTime paidAt;
}
