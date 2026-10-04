package com.ridelink.rideservice.feign;

import com.ridelink.rideservice.dto.FareEstimateRequest;
import com.ridelink.rideservice.dto.FareEstimateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service", url = "${ride.payment-service.url:http://localhost:8084}")
public interface PaymentServiceClient {

    @PostMapping("/api/payments/estimate")
    FareEstimateResponse estimateFare(@RequestBody FareEstimateRequest request);
}
