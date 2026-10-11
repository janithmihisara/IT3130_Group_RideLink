package com.ridelink.rideservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    @Indexed
    private String passengerId;

    @Indexed
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
