package org.example.ivms.service;

import org.example.ivms.dto.SupplierVendorDTO;
import org.example.ivms.dto.VendorRequestDTO;
import org.example.ivms.dto.VendorStatsDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class SupplierPortalService {

    private final WebClient supplierPortalWebClient;

    public SupplierPortalService(WebClient supplierPortalWebClient) {
        this.supplierPortalWebClient = supplierPortalWebClient;
    }

    public List<SupplierVendorDTO> getAllVendors() {
        return supplierPortalWebClient.get()
                .uri("/api/v1/vendors")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SupplierVendorDTO>>() {})
                .block();
    }

    public List<SupplierVendorDTO> getActiveVendors() {
        return supplierPortalWebClient.get()
                .uri("/api/v1/vendors/active")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SupplierVendorDTO>>() {})
                .block();
    }

    public SupplierVendorDTO getVendorById(Long id) {
        return supplierPortalWebClient.get()
                .uri("/api/v1/vendors/{id}", id)
                .retrieve()
                .bodyToMono(SupplierVendorDTO.class)
                .block();
    }

    public List<SupplierVendorDTO> searchVendors(String query) {
        return supplierPortalWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/vendors/search")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<SupplierVendorDTO>>() {})
                .block();
    }

    public VendorStatsDTO getVendorStats() {
        return supplierPortalWebClient.get()
                .uri("/api/v1/vendors/stats")
                .retrieve()
                .bodyToMono(VendorStatsDTO.class)
                .block();
    }

    public Object registerVendor(VendorRequestDTO request) {
        return supplierPortalWebClient.post()
                .uri("/api/v1/vendors/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public String approveVendor(Long id) {
        supplierPortalWebClient.patch()
                .uri("/api/v1/vendors/{id}/approve", id)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "Vendor approved successfully";
    }

    public String updateVendor(Long id, VendorRequestDTO request) {
        supplierPortalWebClient.put()
                .uri("/api/v1/vendors/{id}", id)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "Vendor updated successfully";
    }

    public String blacklistVendor(Long id, String reason) {
        supplierPortalWebClient.patch()
                .uri("/api/v1/vendors/{id}/blacklist", id)
                .bodyValue(Map.of("reason", reason))
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        return "Vendor blacklisted successfully";
    }
}