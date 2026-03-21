package com.example.demo.service;

import com.example.demo.dto.ProductSummaryDTO;
import com.example.demo.dto.InventoryItemDTO; // Your friend's DTO
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProcurementClient {

    private final WebClient webClient;

    public ProcurementClient(WebClient.Builder builder) {
        // Change "8081" to the port your friend is using
        this.webClient = builder.baseUrl("http://localhost:8081").build();
    }

    public void callProcurement(ProductSummaryDTO myProduct) {
        // Map your data into the format his API expects
        InventoryItemDTO friendDto = new InventoryItemDTO();
        friendDto.setId(myProduct.getId());
        friendDto.setName(myProduct.getName());
        friendDto.setSku(myProduct.getSkuCode());
        friendDto.setStock(myProduct.getCurrentStock());
        friendDto.setSafety(myProduct.getReorderThreshold());

        // Async POST request using WebClient
        webClient.post()
                .uri("/api/inventory") // Verify his path in his Controller!
                .bodyValue(friendDto)
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(
                        response -> System.out.println("Successfully notified Procurement: " + response),
                        error -> System.err.println("Failed to notify Procurement: " + error.getMessage())
                );
    }
}