package com.ridelink.rideservice.client;

import com.ridelink.rideservice.dto.DriverDto;
import com.ridelink.rideservice.model.VehicleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class DriverServiceClient {

    private final RestTemplate restTemplate;

    @Value("${driver.service.url:http://localhost:8082}")
    private String driverServiceUrl;

    public DriverServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<DriverDto> getAvailableDrivers(VehicleType vehicleType, String cityName) {
        try {
            String url = driverServiceUrl + "/api/drivers/available?vehicleType=" + (vehicleType != null ? vehicleType.name() : "") +
                    "&cityName=" + (cityName != null ? cityName : "");

            ResponseEntity<List<DriverDto>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<List<DriverDto>>() {});

            return response.getBody() != null ? response.getBody() : Collections.emptyList();
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    public boolean updateDriverAvailability(String driverId, String status) {
        try {
            String url = driverServiceUrl + "/api/drivers/" + driverId + "/availability";
            Map<String, String> request = new HashMap<>();
            request.put("availabilityStatus", status);

            restTemplate.put(url, request);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
