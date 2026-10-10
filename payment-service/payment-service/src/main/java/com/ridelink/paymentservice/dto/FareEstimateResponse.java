package com.ridelink.paymentservice.dto;

import com.ridelink.paymentservice.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateResponse {
    private Double estimatedFare;
    private Double baseFare;
    private Double distanceFare;
    private Double durationFare;
    private Double vehicleTypeMultiplier;
    private VehicleType vehicleType;
    private String calculationFormula;
}
