package com.ridelink.rideservice;

import com.ridelink.rideservice.client.DriverServiceClient;
import com.ridelink.rideservice.client.PaymentServiceClient;
import com.ridelink.rideservice.dto.*;
import com.ridelink.rideservice.exception.InvalidRideStatusTransitionException;
import com.ridelink.rideservice.exception.ResourceNotFoundException;
import com.ridelink.rideservice.model.LocationPoint;
import com.ridelink.rideservice.model.Ride;
import com.ridelink.rideservice.model.RideStatus;
import com.ridelink.rideservice.model.VehicleType;
import com.ridelink.rideservice.repository.RideRepository;
import com.ridelink.rideservice.service.RideServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RideServiceTest {

    @Mock
    private RideRepository rideRepository;

    @Mock
    private DriverServiceClient driverServiceClient;

    @Mock
    private PaymentServiceClient paymentServiceClient;

    @InjectMocks
    private RideServiceImpl rideService;

    private Ride sampleRide;
    private CreateRideRequest createRequest;

    @BeforeEach
    void setUp() {
        LocationPoint pickup = LocationPoint.builder().address("Colombo Fort").latitude(6.9344).longitude(79.8428).city("Colombo").build();
        LocationPoint dest = LocationPoint.builder().address("Bambalapitiya").latitude(6.8887).longitude(79.8585).city("Colombo").build();

        sampleRide = Ride.builder()
                .id("ride-101")
                .passengerId("pass-1")
                .driverId("driver-1")
                .pickupLocation(pickup)
                .destinationLocation(dest)
                .distanceKm(5.0)
                .estimatedDurationMinutes(15.0)
                .vehicleType(VehicleType.SEDAN)
                .estimatedFare(13.00)
                .status(RideStatus.REQUESTED)
                .paymentStatus("PENDING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createRequest = CreateRideRequest.builder()
                .passengerId("pass-1")
                .pickupLocation(pickup)
                .destinationLocation(dest)
                .distanceKm(5.0)
                .estimatedDurationMinutes(15.0)
                .vehicleType(VehicleType.SEDAN)
                .build();
    }

    @Test
    void testCreateRide_Success() {
        when(paymentServiceClient.getFareEstimate(anyDouble(), anyDouble(), any(VehicleType.class))).thenReturn(13.00);
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.createRide(createRequest);

        assertNotNull(response);
        assertEquals(RideStatus.REQUESTED, response.getStatus());
        assertEquals(13.00, response.getEstimatedFare());
        verify(rideRepository, times(1)).save(any(Ride.class));
    }

    @Test
    void testAssignDriver_AutoSelect_Success() {
        when(rideRepository.findById("ride-101")).thenReturn(Optional.of(sampleRide));
        DriverDto availableDriver = DriverDto.builder().driverId("driver-1").name("Alex").build();
        when(driverServiceClient.getAvailableDrivers(any(), any())).thenReturn(Collections.singletonList(availableDriver));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.assignDriver("ride-101", null);

        assertNotNull(response);
        verify(driverServiceClient, times(1)).updateDriverAvailability("driver-1", "BUSY");
    }

    @Test
    void testAcceptRide_Success() {
        sampleRide.setStatus(RideStatus.ASSIGNED);
        when(rideRepository.findById("ride-101")).thenReturn(Optional.of(sampleRide));
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        RideResponse response = rideService.acceptRide("ride-101");

        assertNotNull(response);
        assertEquals(RideStatus.ACCEPTED, response.getStatus());
    }

    @Test
    void testAcceptRide_InvalidTransition() {
        sampleRide.setStatus(RideStatus.REQUESTED); // Cannot accept directly from REQUESTED
        when(rideRepository.findById("ride-101")).thenReturn(Optional.of(sampleRide));

        assertThrows(InvalidRideStatusTransitionException.class, () -> rideService.acceptRide("ride-101"));
    }

    @Test
    void testCompleteRide_Success() {
        sampleRide.setStatus(RideStatus.IN_PROGRESS);
        sampleRide.setEstimatedFare(13.00);
        when(rideRepository.findById("ride-101")).thenReturn(Optional.of(sampleRide));

        PaymentDto mockPayment = PaymentDto.builder().id("pay-999").status("SUCCESS").transactionId("TXN-123").build();
        when(paymentServiceClient.processPayment(anyString(), anyString(), anyString(), anyDouble(), anyString(), any())).thenReturn(mockPayment);
        when(rideRepository.save(any(Ride.class))).thenReturn(sampleRide);

        CompleteRideRequest completeReq = CompleteRideRequest.builder().paymentMethod("CARD").build();
        RideResponse response = rideService.completeRide("ride-101", completeReq);

        assertNotNull(response);
        assertEquals(RideStatus.COMPLETED, response.getStatus());
        verify(driverServiceClient, times(1)).updateDriverAvailability("driver-1", "AVAILABLE");
    }
}
