package com.ridelink.driverservice.controller;

import com.ridelink.driverservice.dto.AvailabilityRequest;
import com.ridelink.driverservice.dto.DriverRequest;
import com.ridelink.driverservice.dto.DriverResponse;
import com.ridelink.driverservice.dto.LocationRequest;
import com.ridelink.driverservice.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    // Create a new driver
    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @Valid @RequestBody DriverRequest request) {

        DriverResponse createdDriver = driverService.createDriver(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDriver);
    }

    // Get all drivers
    @GetMapping
    public ResponseEntity<List<DriverResponse>> getAllDrivers() {

        return ResponseEntity.ok(
                driverService.getAllDrivers()
        );
    }

    // Get driver by ID
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                driverService.getDriver(id)
        );
    }

    // Update driver details
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable Long id,
            @Valid @RequestBody DriverRequest request) {

        return ResponseEntity.ok(
                driverService.updateDriver(id, request)
        );
    }

    // Update driver availability
    @PatchMapping("/{id}/availability")
    public ResponseEntity<DriverResponse> updateAvailability(
            @PathVariable Long id,
            @RequestBody AvailabilityRequest request) {

        return ResponseEntity.ok(
                driverService.updateAvailability(
                        id,
                        request.available()
                )
        );
    }

    // Update driver's current location
    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverResponse> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request) {

        return ResponseEntity.ok(
                driverService.updateLocation(
                        id,
                        request.currentLocation()
                )
        );
    }

    // Get available drivers
    // Example:
    // GET /api/drivers/available
    // GET /api/drivers/available?serviceArea=Colombo
    @GetMapping("/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers(
            @RequestParam(required = false) String serviceArea) {

        return ResponseEntity.ok(
                driverService.getAvailableDrivers(serviceArea)
        );
    }

    // Delete driver
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(
            @PathVariable Long id) {

        driverService.deleteDriver(id);

        return ResponseEntity.noContent().build();
    }
}