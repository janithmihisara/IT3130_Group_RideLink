package com.ridelink.rideservice.dto;

<<<<<<< HEAD
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
=======
import com.ridelink.rideservice.entity.RideStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RideResponse(
    Long id,
    Long passengerId,
    Long driverId,
    String pickupAddress,
    String destinationAddress,
    RideStatus status,
    BigDecimal estimatedFare,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
>>>>>>> edb500c28a5e04acdb764107e0260c57f1adacbb
}
