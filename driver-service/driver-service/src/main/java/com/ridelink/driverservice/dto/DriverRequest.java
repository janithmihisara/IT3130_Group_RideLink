package com.ridelink.driverservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DriverRequest(

        @NotNull(message = "Account ID is required")
        Long accountId,

        @NotBlank(message = "License number is required")
        String licenseNumber,

        @NotBlank(message = "Service area is required")
        String serviceArea,

        String currentLocation,

        boolean available,

        @NotBlank(message = "Vehicle number is required")
        String vehicleNumber,

        @NotBlank(message = "Vehicle model is required")
        String vehicleModel,

        String vehicleColor
) {
}