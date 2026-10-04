package com.ridelink.driverservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    @Column(nullable = false, unique = true)
    private String licenseNumber;

    private String serviceArea;

    private String currentLocation;

    private boolean available;

    private String vehicleNumber;

    private String vehicleModel;

    private String vehicleColor;
}