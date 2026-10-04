package com.ridelink.rideservice.feign;

import com.ridelink.rideservice.dto.DriverSummaryDto;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "driver-service", url = "${ride.driver-service.url:http://localhost:8082}")
public interface DriverServiceClient {

    @GetMapping("/api/drivers/available")
    List<DriverSummaryDto> getAvailableDrivers();
}
