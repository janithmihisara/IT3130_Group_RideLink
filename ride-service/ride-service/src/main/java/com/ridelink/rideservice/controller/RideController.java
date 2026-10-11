package com.ridelink.rideservice.controller;

import com.ridelink.rideservice.dto.AssignDriverRequest;
import com.ridelink.rideservice.dto.CreateRideRequest;
import com.ridelink.rideservice.dto.DriverSummaryDto;
import com.ridelink.rideservice.dto.RideResponse;
import com.ridelink.rideservice.service.RideService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @PostMapping
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(rideService.createRide(request));
    }

    @GetMapping("/{rideId}")
    public ResponseEntity<RideResponse> getRideById(@PathVariable Long rideId) {
        return ResponseEntity.ok(rideService.getRideById(rideId));
    }

    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<RideResponse>> getPassengerRideHistory(@PathVariable Long passengerId) {
        return ResponseEntity.ok(rideService.getPassengerRideHistory(passengerId));
    }

    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<RideResponse>> getDriverRideHistory(@PathVariable Long driverId) {
        return ResponseEntity.ok(rideService.getDriverRideHistory(driverId));
    }

    @GetMapping("/available-drivers")
    public ResponseEntity<List<DriverSummaryDto>> getAvailableDrivers() {
        return ResponseEntity.ok(rideService.getAvailableDrivers());
    }

    @PostMapping("/{rideId}/assign-driver")
    public ResponseEntity<RideResponse> assignDriver(@PathVariable Long rideId, @Valid @RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.assignDriver(rideId, request.driverId()));
    }

    @PostMapping("/{rideId}/accept")
    public ResponseEntity<RideResponse> acceptRide(@PathVariable Long rideId, @Valid @RequestBody AssignDriverRequest request) {
        return ResponseEntity.ok(rideService.acceptRide(rideId, request.driverId()));
    }

    @PostMapping("/{rideId}/start")
    public ResponseEntity<RideResponse> startRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(rideService.startRide(rideId));
    }

    @PostMapping("/{rideId}/complete")
    public ResponseEntity<RideResponse> completeRide(@PathVariable Long rideId) {
        return ResponseEntity.ok(rideService.completeRide(rideId));
    }

    @PostMapping("/{rideId}/cancel")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable Long rideId, @RequestBody(required = false) String reason) {
        return ResponseEntity.ok(rideService.cancelRide(rideId, reason));
    }
}
