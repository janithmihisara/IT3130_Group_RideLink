package com.ridelink.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateDto {
    private Double estimatedFare;
    private Double baseFare;
    private Double distanceFare;
    private Double durationFare;
    private Double vehicleTypeMultiplier;
}
