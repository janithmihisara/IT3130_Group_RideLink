package com.ridelink.rideservice.dto;

import com.ridelink.rideservice.model.LocationPoint;
import com.ridelink.rideservice.model.RideStatus;
import com.ridelink.rideservice.model.VehicleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideResponse {
    private String id;
    private String passengerId;
    private String driverId;
    private LocationPoint pickupLocation;
    private LocationPoint destinationLocation;
    private Double distanceKm;
    private Double estimatedDurationMinutes;
    private VehicleType vehicleType;
    private Double estimatedFare;
    private Double finalFare;
    private RideStatus status;
    private String paymentId;
    private String paymentStatus;
    private String cancellationReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
