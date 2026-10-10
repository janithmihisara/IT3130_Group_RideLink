package com.ridelink.paymentservice.dto;

import com.ridelink.paymentservice.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FareEstimateRequest {

    @NotNull(message = "Distance in KM is required")
    @Min(value = 0, message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Estimated duration in minutes is required")
    @Min(value = 0, message = "Duration must be greater than 0")
    private Double estimatedDurationMinutes;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, BIKE, TUKTUK)")
    private VehicleType vehicleType;
}
