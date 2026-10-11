package com.ridelink.driverservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI driverServiceOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("RideLink Driver Service API")
                        .version("1.0")
                        .description(
                                "Driver and Vehicle Service for RideLink"
                        ));
    }
}