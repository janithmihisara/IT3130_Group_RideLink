package com.ridelink.rideservice.repository;

import com.ridelink.rideservice.entity.Ride;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    List<Ride> findByDriverIdOrderByCreatedAtDesc(Long driverId);
}
