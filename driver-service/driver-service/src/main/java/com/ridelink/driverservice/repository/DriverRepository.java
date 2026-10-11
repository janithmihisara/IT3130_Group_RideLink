package com.ridelink.driverservice.repository;

import com.ridelink.driverservice.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DriverRepository extends JpaRepository<Driver, Long> {

    List<Driver> findByAvailableTrue();

    List<Driver> findByAvailableTrueAndServiceAreaIgnoreCase(String serviceArea);

    Optional<Driver> findByLicenseNumber(String licenseNumber);
}