package com.ridelink.rideservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private String id;
    private String driverId;
    private String name;
    private String phone;
    private String availabilityStatus;
}
