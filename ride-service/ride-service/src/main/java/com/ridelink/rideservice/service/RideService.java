package com.ridelink.rideservice.service;

import com.ridelink.rideservice.dto.*;
import com.ridelink.rideservice.model.RideStatus;

import java.util.List;

public interface RideService {
    RideResponse createRide(CreateRideRequest request);
    RideResponse getRideById(String rideId);
    RideResponse assignDriver(String rideId, AssignDriverRequest request);
    RideResponse acceptRide(String rideId);
    RideResponse startRide(String rideId);
    RideResponse completeRide(String rideId, CompleteRideRequest request);
    RideResponse cancelRide(String rideId, CancelRideRequest request);
    List<RideResponse> getRidesByPassengerId(String passengerId);
    List<RideResponse> getRidesByDriverId(String driverId);
    List<RideResponse> getAllRides(RideStatus statusFilter);
}
