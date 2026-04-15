package com.example.demo.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
//                // 1. Route for Inventory Service
//                .route("inventory_route", r -> r
//                        .path("/inventory/**")
//                        .filters(f -> f.stripPrefix(1))
//                        .uri("lb://InventoryHub"))

                // 2. Route for Order Service
                .route("procument_route", r -> r
                        .path("/procument/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://procument")) // Match the name exactly as it appears in Eureka
                // 2. Route for Order Service
                .route("supplierportal_route", r -> r
                        .path("/supplierportal/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://supplierportal")) // Match the name exactly as it appears in Eureka
                // 2. Route for Order Service
                .route("FinancialLedger_route", r -> r
                        .path("/FinancialLedger/**")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://FinancialLedger")) // Match the name exactly as it appears in Eureka

                .build();
    }
}

