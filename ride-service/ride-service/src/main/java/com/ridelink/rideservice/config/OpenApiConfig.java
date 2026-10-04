package com.ridelink.rideservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI rideLinkOpenApi() {
        return new OpenAPI()
            .info(new Info()
                .title("RideLink Ride Management Service")
                .version("v1.0.0")
                .description("Ride request lifecycle, driver assignment, and fare workflow for RideLink."));
    }
}
