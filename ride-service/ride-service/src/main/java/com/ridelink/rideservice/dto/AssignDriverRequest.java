package com.ridelink.rideservice.dto;

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
}
