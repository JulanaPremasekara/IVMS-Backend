package org.example.ivms.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    /**
     * This bean is the most important part.
     * The @LoadBalanced annotation tells Spring Cloud to use
     * Eureka to resolve service names.
     */
    @Bean
    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryHubWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl("http://INVENTORY") // Use the exact name registered in Eureka
                .build();
    }

    @Bean
    public WebClient procurementWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl("http://Porcurement") // Matches your POM module name
                .build();
    }

    @Bean
    public WebClient supplierPortalWebClient(WebClient.Builder loadBalancedWebClientBuilder) {
        return loadBalancedWebClientBuilder
                .baseUrl("http://SupplierPortal")
                .build();
    }
}