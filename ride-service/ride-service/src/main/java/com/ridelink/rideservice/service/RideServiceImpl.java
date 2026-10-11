package com.ridelink.rideservice.service;

import com.ridelink.rideservice.client.DriverServiceClient;
import com.ridelink.rideservice.client.PaymentServiceClient;
import com.ridelink.rideservice.dto.*;
import com.ridelink.rideservice.exception.InvalidRideStatusTransitionException;
import com.ridelink.rideservice.exception.NoAvailableDriverException;
import com.ridelink.rideservice.exception.ResourceNotFoundException;
import com.ridelink.rideservice.model.Ride;
import com.ridelink.rideservice.model.RideStatus;
import com.ridelink.rideservice.repository.RideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RideServiceImpl implements RideService {

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Autowired
    public RideServiceImpl(RideRepository rideRepository,
                           DriverServiceClient driverServiceClient,
                           PaymentServiceClient paymentServiceClient) {
        this.rideRepository = rideRepository;
        this.driverServiceClient = driverServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public RideResponse createRide(CreateRideRequest request) {
        Double estimatedFare = paymentServiceClient.getFareEstimate(
                request.getDistanceKm(), request.getEstimatedDurationMinutes(), request.getVehicleType());

        Ride ride = Ride.builder()
                .passengerId(request.getPassengerId())
                .pickupLocation(request.getPickupLocation())
                .destinationLocation(request.getDestinationLocation())
                .distanceKm(request.getDistanceKm())
                .estimatedDurationMinutes(request.getEstimatedDurationMinutes())
                .vehicleType(request.getVehicleType())
                .estimatedFare(estimatedFare)
                .status(RideStatus.REQUESTED)
                .paymentStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Ride saved = rideRepository.save(ride);
        return mapToRideResponse(saved);
    }

    @Override
    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
        return mapToRideResponse(ride);
    }

    @Override
    public RideResponse assignDriver(String rideId, AssignDriverRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new InvalidRideStatusTransitionException("Cannot assign driver to ride in state: " + ride.getStatus());
        }

        String assignedDriverId = null;
        if (request != null && request.getDriverId() != null && !request.getDriverId().isBlank()) {
            assignedDriverId = request.getDriverId();
        } else {
            String city = ride.getPickupLocation() != null ? ride.getPickupLocation().getCity() : null;
            List<DriverDto> availableDrivers = driverServiceClient.getAvailableDrivers(ride.getVehicleType(), city);
            if (availableDrivers.isEmpty()) {
                throw new NoAvailableDriverException("No available drivers found for vehicle type: " + ride.getVehicleType());
            }
            assignedDriverId = availableDrivers.get(0).getDriverId();
        }

        ride.setDriverId(assignedDriverId);
        ride.setStatus(RideStatus.ASSIGNED);
        ride.setUpdatedAt(LocalDateTime.now());

        // Update driver availability in Driver Service
        driverServiceClient.updateDriverAvailability(assignedDriverId, "BUSY");

        Ride updated = rideRepository.save(ride);
        return mapToRideResponse(updated);
    }

    @Override
    public RideResponse acceptRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new InvalidRideStatusTransitionException("Ride cannot be accepted from state: " + ride.getStatus() + ". Must be ASSIGNED.");
        }

        ride.setStatus(RideStatus.ACCEPTED);
        ride.setUpdatedAt(LocalDateTime.now());

        Ride updated = rideRepository.save(ride);
        return mapToRideResponse(updated);
    }

    @Override
    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new InvalidRideStatusTransitionException("Ride cannot be started from state: " + ride.getStatus() + ". Must be ACCEPTED.");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setUpdatedAt(LocalDateTime.now());

        Ride updated = rideRepository.save(ride);
        return mapToRideResponse(updated);
    }

    @Override
    public RideResponse completeRide(String rideId, CompleteRideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() != RideStatus.IN_PROGRESS) {
            throw new InvalidRideStatusTransitionException("Ride cannot be completed from state: " + ride.getStatus() + ". Must be IN_PROGRESS.");
        }

        Double finalFare = ride.getEstimatedFare();
        ride.setFinalFare(finalFare);

        // Process payment via Payment Service
        String method = (request != null && request.getPaymentMethod() != null) ? request.getPaymentMethod() : "CARD";
        Boolean simulateFail = request != null && Boolean.TRUE.equals(request.getSimulatePaymentFailure());

        PaymentDto payment = paymentServiceClient.processPayment(
                ride.getId(), ride.getPassengerId(), ride.getDriverId(), finalFare, method, simulateFail);

        if (payment != null && "SUCCESS".equalsIgnoreCase(payment.getStatus())) {
            ride.setPaymentId(payment.getId() != null ? payment.getId() : payment.getTransactionId());
            ride.setPaymentStatus("SUCCESS");
            ride.setStatus(RideStatus.COMPLETED);
            ride.setUpdatedAt(LocalDateTime.now());

            // Free the driver in Driver Service
            if (ride.getDriverId() != null) {
                driverServiceClient.updateDriverAvailability(ride.getDriverId(), "AVAILABLE");
            }

            Ride completed = rideRepository.save(ride);
            return mapToRideResponse(completed);
        } else {
            ride.setPaymentStatus("FAILED");
            rideRepository.save(ride);
            throw new InvalidRideStatusTransitionException("Payment failed for ride completion.");
        }
    }

    @Override
    public RideResponse cancelRide(String rideId, CancelRideRequest request) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));

        if (ride.getStatus() == RideStatus.COMPLETED || ride.getStatus() == RideStatus.CANCELLED) {
            throw new InvalidRideStatusTransitionException("Cannot cancel ride that is already in state: " + ride.getStatus());
        }

        String driverIdToFree = ride.getDriverId();

        ride.setStatus(RideStatus.CANCELLED);
        ride.setCancellationReason(request != null ? request.getCancellationReason() : "Cancelled by user");
        ride.setUpdatedAt(LocalDateTime.now());

        if (driverIdToFree != null) {
            driverServiceClient.updateDriverAvailability(driverIdToFree, "AVAILABLE");
        }

        Ride cancelled = rideRepository.save(ride);
        return mapToRideResponse(cancelled);
    }

    @Override
    public List<RideResponse> getRidesByPassengerId(String passengerId) {
        return rideRepository.findByPassengerId(passengerId).stream().map(this::mapToRideResponse).collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getRidesByDriverId(String driverId) {
        return rideRepository.findByDriverId(driverId).stream().map(this::mapToRideResponse).collect(Collectors.toList());
    }

    @Override
    public List<RideResponse> getAllRides(RideStatus statusFilter) {
        List<Ride> rides = (statusFilter != null) ? rideRepository.findByStatus(statusFilter) : rideRepository.findAll();
        return rides.stream().map(this::mapToRideResponse).collect(Collectors.toList());
    }

    private RideResponse mapToRideResponse(Ride ride) {
        return RideResponse.builder()
                .id(ride.getId())
                .passengerId(ride.getPassengerId())
                .driverId(ride.getDriverId())
                .pickupLocation(ride.getPickupLocation())
                .destinationLocation(ride.getDestinationLocation())
                .distanceKm(ride.getDistanceKm())
                .estimatedDurationMinutes(ride.getEstimatedDurationMinutes())
                .vehicleType(ride.getVehicleType())
                .estimatedFare(ride.getEstimatedFare())
                .finalFare(ride.getFinalFare())
                .status(ride.getStatus())
                .paymentId(ride.getPaymentId())
                .paymentStatus(ride.getPaymentStatus())
                .cancellationReason(ride.getCancellationReason())
                .createdAt(ride.getCreatedAt())
                .updatedAt(ride.getUpdatedAt())
                .build();
    }
}
