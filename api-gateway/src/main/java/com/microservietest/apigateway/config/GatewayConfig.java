package com.microservietest.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("USER-SERVICE",
                route -> route.path("/user/**")
                    .filters(filter -> filter.stripPrefix(0)
                    )
                    .uri("lb://USER-SERVICE"))
            .route("DEPARTMENT-SERVICE",
                route -> route.path("/department/**")
                    .filters(filter -> filter.stripPrefix(0)
                    )
                    .uri("lb://DEPARTMENT-SERVICE"))
            .route("OFFICE-SERVICE",
                route -> route.path("/office/**")
                    .filters(filter -> filter.stripPrefix(0)
                    )
                    .uri("lb://OFFICE-SERVICE"))
            .build();
    }
}
