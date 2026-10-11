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
public class ReceiptResponse {
    private String receiptNumber;
    private String transactionId;
    private String rideId;
    private String passengerId;
    private String driverId;
    private Double amount;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime paidAt;
    private String companyName;
}
