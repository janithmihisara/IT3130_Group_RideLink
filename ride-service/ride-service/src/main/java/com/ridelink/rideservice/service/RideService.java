package com.ridelink.rideservice.service;

import com.ridelink.rideservice.dto.CreateRideRequest;
import com.ridelink.rideservice.dto.DriverSummaryDto;
import com.ridelink.rideservice.dto.FareEstimateRequest;
import com.ridelink.rideservice.dto.FareEstimateResponse;
import com.ridelink.rideservice.dto.RideResponse;
import com.ridelink.rideservice.entity.Ride;
import com.ridelink.rideservice.entity.RideStatus;
import com.ridelink.rideservice.exception.DriverUnavailableException;
import com.ridelink.rideservice.exception.InvalidRideStateException;
import com.ridelink.rideservice.exception.RideNotFoundException;
import com.ridelink.rideservice.feign.DriverServiceClient;
import com.ridelink.rideservice.feign.PaymentServiceClient;
import com.ridelink.rideservice.repository.RideRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RideService {

    private static final Map<RideStatus, EnumSet<RideStatus>> ALLOWED_TRANSITIONS = Map.of(
        RideStatus.REQUESTED, EnumSet.of(RideStatus.ASSIGNED, RideStatus.CANCELLED),
        RideStatus.ASSIGNED, EnumSet.of(RideStatus.ACCEPTED, RideStatus.CANCELLED),
        RideStatus.ACCEPTED, EnumSet.of(RideStatus.IN_PROGRESS, RideStatus.CANCELLED),
        RideStatus.IN_PROGRESS, EnumSet.of(RideStatus.COMPLETED, RideStatus.CANCELLED),
        RideStatus.COMPLETED, EnumSet.noneOf(RideStatus.class),
        RideStatus.CANCELLED, EnumSet.noneOf(RideStatus.class)
    );

    private final RideRepository rideRepository;
    private final DriverServiceClient driverServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    @Transactional
    public RideResponse createRide(CreateRideRequest request) {
        Ride ride = Ride.builder()
            .passengerId(request.passengerId())
            .pickupAddress(request.pickupAddress())
            .destinationAddress(request.destinationAddress())
            .pickupLatitude(request.pickupLatitude())
            .pickupLongitude(request.pickupLongitude())
            .destinationLatitude(request.destinationLatitude())
            .destinationLongitude(request.destinationLongitude())
            .status(RideStatus.REQUESTED)
            .fareEstimate(BigDecimal.ZERO)
            .build();

        Ride savedRide = rideRepository.save(ride);

        FareEstimateRequest fareRequest = new FareEstimateRequest(
            request.passengerId(),
            request.pickupLatitude(),
            request.pickupLongitude(),
            request.destinationLatitude(),
            request.destinationLongitude()
        );

        FareEstimateResponse fareResponse = paymentServiceClient.estimateFare(fareRequest);
        if (fareResponse != null && fareResponse.estimatedFare() != null) {
            savedRide.setFareEstimate(fareResponse.estimatedFare().setScale(2, RoundingMode.HALF_UP));
            savedRide = rideRepository.save(savedRide);
        }

        return toResponse(savedRide);
    }

    @Transactional
    public RideResponse assignDriver(Long rideId, Long driverId) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.ASSIGNED);

        if (driverId == null) {
            throw new DriverUnavailableException("Driver ID is required for assignment.");
        }

        boolean driverAvailable = driverServiceClient.getAvailableDrivers().stream()
            .anyMatch(driver -> driver.id().equals(driverId) && driver.available());

        if (!driverAvailable) {
            throw new DriverUnavailableException("Driver " + driverId + " is not available for assignment.");
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse assignAvailableDriver(Long rideId) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.ASSIGNED);

        List<DriverSummaryDto> availableDrivers = driverServiceClient.getAvailableDrivers();
        if (availableDrivers == null || availableDrivers.isEmpty()) {
            throw new DriverUnavailableException("No available drivers found at the moment.");
        }

        DriverSummaryDto selectedDriver = availableDrivers.stream()
            .filter(DriverSummaryDto::available)
            .findFirst()
            .orElseThrow(() -> new DriverUnavailableException("No available drivers found at the moment."));

        ride.setDriverId(selectedDriver.id());
        ride.setStatus(RideStatus.ASSIGNED);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse acceptRide(Long rideId, Long driverId) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.ACCEPTED);

        if (ride.getDriverId() == null || !Objects.equals(ride.getDriverId(), driverId)) {
            throw new InvalidRideStateException("Ride " + rideId + " is assigned to another driver.");
        }

        ride.setStatus(RideStatus.ACCEPTED);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse startRide(Long rideId) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.IN_PROGRESS);
        ride.setStatus(RideStatus.IN_PROGRESS);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse completeRide(Long rideId) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.COMPLETED);
        ride.setStatus(RideStatus.COMPLETED);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional
    public RideResponse cancelRide(Long rideId, String reason) {
        Ride ride = getRequiredRide(rideId);
        validateTransition(ride, RideStatus.CANCELLED);
        ride.setStatus(RideStatus.CANCELLED);
        return toResponse(rideRepository.save(ride));
    }

    @Transactional(readOnly = true)
    public RideResponse getRideById(Long rideId) {
        return toResponse(getRequiredRide(rideId));
    }

    @Transactional(readOnly = true)
    public List<RideResponse> getPassengerRideHistory(Long passengerId) {
        return rideRepository.findByPassengerIdOrderByCreatedAtDesc(passengerId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<RideResponse> getDriverRideHistory(Long driverId) {
        return rideRepository.findByDriverIdOrderByCreatedAtDesc(driverId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<DriverSummaryDto> getAvailableDrivers() {
        return driverServiceClient.getAvailableDrivers();
    }

    private Ride getRequiredRide(Long rideId) {
        return rideRepository.findById(rideId)
            .orElseThrow(() -> new RideNotFoundException(rideId));
    }

    private void validateTransition(Ride ride, RideStatus nextStatus) {
        RideStatus current = ride.getStatus();
        EnumSet<RideStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, EnumSet.noneOf(RideStatus.class));

        if (!allowed.contains(nextStatus)) {
            throw new InvalidRideStateException(
                "Cannot transition ride " + ride.getId() + " from " + current + " to " + nextStatus + "."
            );
        }
    }

    private RideResponse toResponse(Ride ride) {
        return new RideResponse(
            ride.getId(),
            ride.getPassengerId(),
            ride.getDriverId(),
            ride.getPickupAddress(),
            ride.getDestinationAddress(),
            ride.getStatus(),
            ride.getFareEstimate(),
            ride.getCreatedAt(),
            ride.getUpdatedAt()
        );
    }
}
