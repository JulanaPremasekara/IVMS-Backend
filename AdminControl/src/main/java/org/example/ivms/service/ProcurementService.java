package org.example.ivms.service;

import org.example.ivms.dto.ProcurementIncomingShipmentDTO;
import org.example.ivms.dto.ProcurementPurchaseOrderDTO;
import org.example.ivms.dto.ProcurementShipmentDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class ProcurementService {

    private final WebClient procurementWebClient;

    public ProcurementService(WebClient procurementWebClient) {
        this.procurementWebClient = procurementWebClient;
    }

    public List<ProcurementPurchaseOrderDTO> getAllPurchaseOrders() {
        return procurementWebClient.get()
                .uri("/api/v1/purchaseOrders")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProcurementPurchaseOrderDTO>>() {})
                .block();
    }

    public List<ProcurementShipmentDTO> getAllShipments() {
        return procurementWebClient.get()
                .uri("/api/v1/shipments")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProcurementShipmentDTO>>() {})
                .block();
    }

    public List<ProcurementIncomingShipmentDTO> getIncomingShipments() {
        return procurementWebClient.get()
                .uri("/api/v1/shipments/incoming")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<ProcurementIncomingShipmentDTO>>() {})
                .block();
    }

    public String approvePurchaseOrder(String poNumber, String supplierUser) {
        return procurementWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/purchaseOrders/{id}/approve")
                        .queryParam("supplierUser", supplierUser)
                        .build(poNumber))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}