package com.ridelink.rideservice.dto;

<<<<<<< HEAD
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignDriverRequest {
    private String driverId; // Optional: If provided, assigns this driver; if null, selects first available driver
=======
import jakarta.validation.constraints.NotNull;

public record AssignDriverRequest(@NotNull(message = "Driver ID is required") Long driverId) {
>>>>>>> edb500c28a5e04acdb764107e0260c57f1adacbb
}
