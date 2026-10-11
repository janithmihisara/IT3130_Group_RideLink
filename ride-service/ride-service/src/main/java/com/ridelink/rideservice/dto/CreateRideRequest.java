package com.ridelink.rideservice.dto;

<<<<<<< HEAD
import com.ridelink.rideservice.model.LocationPoint;
import com.ridelink.rideservice.model.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateRideRequest {

    @NotBlank(message = "Passenger ID is required")
    private String passengerId;

    @NotNull(message = "Pickup location is required")
    private LocationPoint pickupLocation;

    @NotNull(message = "Destination location is required")
    private LocationPoint destinationLocation;

    @NotNull(message = "Distance in KM is required")
    @Min(value = 0, message = "Distance must be greater than 0")
    private Double distanceKm;

    @NotNull(message = "Estimated duration in minutes is required")
    @Min(value = 0, message = "Duration must be greater than 0")
    private Double estimatedDurationMinutes;

    @NotNull(message = "Vehicle type is required (SEDAN, SUV, BIKE, TUKTUK)")
    private VehicleType vehicleType;
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreateRideRequest(
    @NotNull(message = "Passenger ID is required") Long passengerId,
    @NotBlank(message = "Pickup address is required") String pickupAddress,
    @NotBlank(message = "Destination address is required") String destinationAddress,
    @NotNull(message = "Pickup latitude is required") BigDecimal pickupLatitude,
    @NotNull(message = "Pickup longitude is required") BigDecimal pickupLongitude,
    @NotNull(message = "Destination latitude is required") BigDecimal destinationLatitude,
    @NotNull(message = "Destination longitude is required") BigDecimal destinationLongitude
) {
>>>>>>> edb500c28a5e04acdb764107e0260c57f1adacbb
}
