package com.ridelink.driverservice.dto;

public record DriverResponse(
        Long id,
        Long accountId,
        String licenseNumber,
        String serviceArea,
        String currentLocation,
        boolean available,
        String vehicleNumber,
        String vehicleModel,
        String vehicleColor
) {
}