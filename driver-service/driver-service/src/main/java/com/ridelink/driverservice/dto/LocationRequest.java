package com.ridelink.driverservice.dto;

import jakarta.validation.constraints.NotBlank;

public record LocationRequest(

        @NotBlank(message = "Location is required")
        String currentLocation
) {
}