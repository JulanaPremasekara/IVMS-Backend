package com.example.demo.config;

import com.example.demo.dto.InventoryItemResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class InventoryClient {
    private final WebClient.Builder webClientBuilder;

    public InventoryClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public InventoryItemResponse getInventoryItemResponse(Long productId) {
        return webClientBuilder.build()
                .get()
                .uri("lb://INVENTORY/api/v1/products/Item/{id}",productId)
                .retrieve()
                .bodyToMono(InventoryItemResponse.class)
                .block();
    }
}
