package com.ridelink.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDto {
    private String id;
    private String rideId;
    private String transactionId;
    private String receiptNumber;
    private String status;
}
