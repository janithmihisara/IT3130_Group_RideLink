package com.ridelink.rideservice.repository;

<<<<<<< HEAD
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
=======
import com.ridelink.rideservice.entity.Ride;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRepository extends JpaRepository<Ride, Long> {

    List<Ride> findByPassengerIdOrderByCreatedAtDesc(Long passengerId);

    List<Ride> findByDriverIdOrderByCreatedAtDesc(Long driverId);
>>>>>>> edb500c28a5e04acdb764107e0260c57f1adacbb
}
