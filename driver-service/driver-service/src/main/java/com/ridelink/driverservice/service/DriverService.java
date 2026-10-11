package com.ridelink.driverservice.service;

import com.ridelink.driverservice.dto.DriverRequest;
import com.ridelink.driverservice.dto.DriverResponse;
import com.ridelink.driverservice.entity.Driver;
import com.ridelink.driverservice.exception.DriverNotFoundException;
import com.ridelink.driverservice.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public DriverResponse createDriver(DriverRequest request) {

        Driver driver = Driver.builder()
                .accountId(request.accountId())
                .licenseNumber(request.licenseNumber())
                .serviceArea(request.serviceArea())
                .currentLocation(request.currentLocation())
                .available(request.available())
                .vehicleNumber(request.vehicleNumber())
                .vehicleModel(request.vehicleModel())
                .vehicleColor(request.vehicleColor())
                .build();

        return mapToResponse(driverRepository.save(driver));
    }

    public List<DriverResponse> getAllDrivers() {
        return driverRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public DriverResponse getDriver(Long id) {
        return mapToResponse(findDriver(id));
    }

    public DriverResponse updateDriver(Long id, DriverRequest request) {

        Driver driver = findDriver(id);

        driver.setAccountId(request.accountId());
        driver.setLicenseNumber(request.licenseNumber());
        driver.setServiceArea(request.serviceArea());
        driver.setCurrentLocation(request.currentLocation());
        driver.setAvailable(request.available());
        driver.setVehicleNumber(request.vehicleNumber());
        driver.setVehicleModel(request.vehicleModel());
        driver.setVehicleColor(request.vehicleColor());

        return mapToResponse(driverRepository.save(driver));
    }

    public DriverResponse updateAvailability(Long id, boolean available) {

        Driver driver = findDriver(id);

        driver.setAvailable(available);

        return mapToResponse(driverRepository.save(driver));
    }

    public DriverResponse updateLocation(Long id, String location) {

        Driver driver = findDriver(id);

        driver.setCurrentLocation(location);

        return mapToResponse(driverRepository.save(driver));
    }

    public List<DriverResponse> getAvailableDrivers(String serviceArea) {

        List<Driver> drivers;

        if (serviceArea == null || serviceArea.isBlank()) {
            drivers = driverRepository.findByAvailableTrue();
        } else {
            drivers = driverRepository
                    .findByAvailableTrueAndServiceAreaIgnoreCase(serviceArea);
        }

        return drivers.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteDriver(Long id) {

        Driver driver = findDriver(id);

        driverRepository.delete(driver);
    }

    private Driver findDriver(Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));
    }

    private DriverResponse mapToResponse(Driver driver) {
        return new DriverResponse(
                driver.getId(),
                driver.getAccountId(),
                driver.getLicenseNumber(),
                driver.getServiceArea(),
                driver.getCurrentLocation(),
                driver.isAvailable(),
                driver.getVehicleNumber(),
                driver.getVehicleModel(),
                driver.getVehicleColor()
        );
    }
}