package com.ridelink.rideservice.client;

import com.ridelink.rideservice.dto.FareEstimateDto;
import com.ridelink.rideservice.dto.PaymentDto;
import com.ridelink.rideservice.model.VehicleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${payment.service.url:http://localhost:8084}")
    private String paymentServiceUrl;

    public PaymentServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Double getFareEstimate(Double distanceKm, Double durationMinutes, VehicleType vehicleType) {
        try {
            String url = paymentServiceUrl + "/api/fares/estimate";
            Map<String, Object> request = new HashMap<>();
            request.put("distanceKm", distanceKm);
            request.put("estimatedDurationMinutes", durationMinutes);
            request.put("vehicleType", vehicleType != null ? vehicleType.name() : "SEDAN");

            ResponseEntity<FareEstimateDto> response = restTemplate.postForEntity(url, request, FareEstimateDto.class);
            if (response.getBody() != null && response.getBody().getEstimatedFare() != null) {
                return response.getBody().getEstimatedFare();
            }
        } catch (Exception ex) {
            // Fallback calculation if Payment Service is down during standalone testing
            double base = 2.50;
            double mult = vehicleType == VehicleType.SUV ? 1.5 : (vehicleType == VehicleType.BIKE ? 0.8 : 1.0);
            return (base + (distanceKm * 1.20) + (durationMinutes * 0.30)) * mult;
        }
        return 15.0;
    }

    public PaymentDto processPayment(String rideId, String passengerId, String driverId, Double amount, String paymentMethod, Boolean simulateFailure) {
        String url = paymentServiceUrl + "/api/payments/process";
        Map<String, Object> request = new HashMap<>();
        request.put("rideId", rideId);
        request.put("passengerId", passengerId);
        request.put("driverId", driverId);
        request.put("amount", amount);
        request.put("paymentMethod", paymentMethod);
        request.put("simulateFailure", simulateFailure);

        ResponseEntity<PaymentDto> response = restTemplate.postForEntity(url, request, PaymentDto.class);
        return response.getBody();
    }
}
