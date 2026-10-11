package com.ridelink.rideservice.controller;

import com.ridelink.rideservice.dto.*;
import com.ridelink.rideservice.model.RideStatus;
import com.ridelink.rideservice.service.RideService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rides")
@Tag(name = "Ride Management", description = "Endpoints for Ride Lifecycle (Request, Driver Assignment, Acceptance, Start, Completion, Cancellation)")
public class RideController {

    private final RideService rideService;

    public RideController(RideService rideService) {
        this.rideService = rideService;
    }

    @PostMapping("/request")
    @Operation(summary = "Create ride request", description = "Passenger submits pickup, destination, distance, duration & vehicleType; receives fare estimate.")
    public ResponseEntity<RideResponse> createRide(@Valid @RequestBody CreateRideRequest request) {
        RideResponse response = rideService.createRide(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get ride details by ID", description = "Retrieves current status and details of a ride.")
    public ResponseEntity<RideResponse> getRideById(@PathVariable("id") String id) {
        RideResponse response = rideService.getRideById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assign driver to ride", description = "Assigns specified driver or auto-selects available driver from Driver Service.")
    public ResponseEntity<RideResponse> assignDriver(@PathVariable("id") String id,
                                                     @RequestBody(required = false) AssignDriverRequest request) {
        RideResponse response = rideService.assignDriver(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/accept")
    @Operation(summary = "Driver accepts ride", description = "Driver accepts assigned ride (transitions status from ASSIGNED to ACCEPTED).")
    public ResponseEntity<RideResponse> acceptRide(@PathVariable("id") String id) {
        RideResponse response = rideService.acceptRide(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Start ride", description = "Driver starts the trip (transitions status from ACCEPTED to IN_PROGRESS).")
    public ResponseEntity<RideResponse> startRide(@PathVariable("id") String id) {
        RideResponse response = rideService.startRide(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Complete ride and trigger payment", description = "Driver completes trip, triggers simulated payment, and frees driver (status -> COMPLETED).")
    public ResponseEntity<RideResponse> completeRide(@PathVariable("id") String id,
                                                     @RequestBody(required = false) CompleteRideRequest request) {
        RideResponse response = rideService.completeRide(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel ride", description = "Passenger or driver cancels ride, providing cancellation reason (status -> CANCELLED).")
    public ResponseEntity<RideResponse> cancelRide(@PathVariable("id") String id,
                                                   @Valid @RequestBody CancelRideRequest request) {
        RideResponse response = rideService.cancelRide(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/passenger/{passengerId}")
    @Operation(summary = "Get ride history for passenger", description = "Retrieves all ride records created by passenger ID.")
    public ResponseEntity<List<RideResponse>> getRidesByPassengerId(@PathVariable("passengerId") String passengerId) {
        List<RideResponse> rides = rideService.getRidesByPassengerId(passengerId);
        return ResponseEntity.ok(rides);
    }

    @GetMapping("/driver/{driverId}")
    @Operation(summary = "Get ride history for driver", description = "Retrieves all ride records assigned to driver ID.")
    public ResponseEntity<List<RideResponse>> getRidesByDriverId(@PathVariable("driverId") String driverId) {
        List<RideResponse> rides = rideService.getRidesByDriverId(driverId);
        return ResponseEntity.ok(rides);
    }

    @GetMapping
    @Operation(summary = "Get all rides", description = "Retrieves list of all rides with optional status filter.")
    public ResponseEntity<List<RideResponse>> getAllRides(@RequestParam(required = false) RideStatus status) {
        List<RideResponse> rides = rideService.getAllRides(status);
        return ResponseEntity.ok(rides);
    }
}
