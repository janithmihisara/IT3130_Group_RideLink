package com.ridelink.rideservice.dto;

import jakarta.validation.constraints.NotNull;

public record AssignDriverRequest(@NotNull(message = "Driver ID is required") Long driverId) {
}
