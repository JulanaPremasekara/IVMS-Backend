package com.example.demo.config;

import com.example.demo.dto.VendorResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class VendorClient {

    private final WebClient.Builder webClientBuilder;

    public VendorClient(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public VendorResponse getVendor(Long id) {
        return webClientBuilder.build()
                .get()
                .uri("lb://SUPPLIERPORTAL/api/v1/vendors/{id}",id )
                .retrieve()
                .bodyToMono(VendorResponse.class)
                .block();
    }
}
