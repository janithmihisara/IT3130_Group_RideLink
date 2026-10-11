package com.ridelink.rideservice.repository;

import com.ridelink.rideservice.model.Ride;
import com.ridelink.rideservice.model.RideStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RideRepository extends MongoRepository<Ride, String> {
    List<Ride> findByPassengerId(String passengerId);
    List<Ride> findByDriverId(String driverId);
    List<Ride> findByStatus(RideStatus status);
}
