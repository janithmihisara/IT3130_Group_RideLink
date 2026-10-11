package com.ridelink.rideservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.ridelink.rideservice.dto.CreateRideRequest;
import com.ridelink.rideservice.dto.DriverSummaryDto;
import com.ridelink.rideservice.dto.FareEstimateRequest;
import com.ridelink.rideservice.dto.FareEstimateResponse;
import com.ridelink.rideservice.dto.RideResponse;
import com.ridelink.rideservice.entity.Ride;
import com.ridelink.rideservice.entity.RideStatus;
import com.ridelink.rideservice.exception.InvalidRideStateException;
import com.ridelink.rideservice.feign.DriverServiceClient;
import com.ridelink.rideservice.feign.PaymentServiceClient;
import com.ridelink.rideservice.repository.RideRepository;
import com.ridelink.rideservice.service.RideService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RideServiceLifecycleTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private RideService rideService;

    private Ride existingRide;

    @BeforeEach
    void setUp() {
        existingRide = Ride.builder()
            .id(1L)
            .passengerId(10L)
            .driverId(200L)
            .pickupAddress("Downtown")
            .destinationAddress("Airport")
            .pickupLatitude(BigDecimal.valueOf(6.5244))
            .pickupLongitude(BigDecimal.valueOf(3.3792))
            .destinationLatitude(BigDecimal.valueOf(6.6018))
            .destinationLongitude(BigDecimal.valueOf(3.3517))
            .status(RideStatus.ASSIGNED)
            .fareEstimate(BigDecimal.valueOf(42.50))
            .build();
    }

    @Test
    void shouldAssignDriverAndTransitionToAssigned() {
        Ride ride = Ride.builder()
            .id(1L)
            .passengerId(10L)
            .pickupAddress("Downtown")
            .destinationAddress("Airport")
            .pickupLatitude(BigDecimal.valueOf(6.5244))
            .pickupLongitude(BigDecimal.valueOf(3.3792))
            .destinationLatitude(BigDecimal.valueOf(6.6018))
            .destinationLongitude(BigDecimal.valueOf(3.3517))
            .status(RideStatus.REQUESTED)
            .fareEstimate(BigDecimal.ZERO)
            .build();

        when(rideRepository.findById(1L)).thenReturn(Optional.of(ride));
        when(driverServiceClient.getAvailableDrivers()).thenReturn(List.of(new DriverSummaryDto(200L, "Jane", true, 4.8)));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.assignDriver(1L, 200L);

        assertEquals(RideStatus.ASSIGNED, response.status());
        assertEquals(200L, response.driverId());
    }

    @Test
    void shouldCompleteRideFromInProgress() {
        existingRide.setStatus(RideStatus.IN_PROGRESS);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(existingRide));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> invocation.getArgument(0));

        RideResponse response = rideService.completeRide(1L);

        assertEquals(RideStatus.COMPLETED, response.status());
    }

    @Test
    void shouldRejectInvalidStateTransition() {
        existingRide.setStatus(RideStatus.COMPLETED);
        when(rideRepository.findById(1L)).thenReturn(Optional.of(existingRide));

        InvalidRideStateException exception = assertThrows(
            InvalidRideStateException.class,
            () -> rideService.cancelRide(1L, "No longer needed")
        );

        assertTrue(exception.getMessage().contains("Cannot transition"));
    }

    @Test
    void shouldEstimateFareWhenCreatingRide() {
        CreateRideRequest request = new CreateRideRequest(
            10L,
            "Downtown",
            "Airport",
            BigDecimal.valueOf(6.5244),
            BigDecimal.valueOf(3.3792),
            BigDecimal.valueOf(6.6018),
            BigDecimal.valueOf(3.3517)
        );

        when(paymentServiceClient.estimateFare(any(FareEstimateRequest.class)))
            .thenReturn(new FareEstimateResponse(BigDecimal.valueOf(45.00)));
        when(rideRepository.save(any(Ride.class))).thenAnswer(invocation -> {
            Ride ride = invocation.getArgument(0);
            ride.setId(1L);
            return ride;
        });

        RideResponse response = rideService.createRide(request);

        assertNotNull(response);
        assertEquals(RideStatus.REQUESTED, response.status());
        assertEquals(new BigDecimal("45.00"), response.estimatedFare());
    }
}
