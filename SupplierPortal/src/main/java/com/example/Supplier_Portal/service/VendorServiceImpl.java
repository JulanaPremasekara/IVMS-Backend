package com.example.Supplier_Portal.service;

import com.example.Supplier_Portal.dto.VendorRequest;
import com.example.Supplier_Portal.model.Vendor;
import com.example.Supplier_Portal.repository.VendorRepository;
import com.example.Supplier_Portal.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;

    @Override
    @Transactional
    public Map<String, Object> registerVendor(VendorRequest request) {
        // Initializing a new vendor with 0% metrics for the analytics dashboard
        Vendor vendor = Vendor.builder()
                .companyName(request.getCompanyName())
                .taxId(request.getTaxId())
                .address(request.getAddress())
                .contactPerson(request.getContactPerson())
                .email(request.getEmail())
                .phone(request.getPhone())
                .paymentTerms(request.getPaymentTerms())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .sku(request.getSku())
                .unitPrice(request.getUnitPrice())
                .status("PENDING")
                // --- INITIALIZING ANALYTICS BASELINE ---
                .performanceScore(0.0)      // Sets "Vendor Score" to 0%
                .avgLeadTime(0.0)           // Sets "Avg Lead Time" to 0
                .delayProbability(0)        // Sets "Delay Probability" to 0%
                .reliabilityRating(0.0)     // Sets Rating stars to empty
                .totalOrders(0)             // Logic trigger for "New Vendor" view
                .build();

        Vendor savedVendor = vendorRepository.save(vendor);

        Map<String, Object> response = new HashMap<>();
        response.put("id", savedVendor.getId());
        response.put("message", "Vendor registered successfully. Awaiting approval.");
        response.put("status", 201);

        return response;
    }

    @Override
    @Transactional
    public void approveVendor(Long id) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        vendor.setStatus("APPROVED");
        vendor.setBlacklistReason(null); // Clears reason if restoring a blacklisted vendor

        // Ensure metrics are initialized if they are null during the transition
        if (vendor.getTotalOrders() == null) {
            vendor.setTotalOrders(0);
            vendor.setPerformanceScore(0.0);
            vendor.setAvgLeadTime(0.0);
            vendor.setDelayProbability(0);
            vendor.setReliabilityRating(0.0);
        }

        vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getAllVendors() {
        return vendorRepository.findAllByOrderByIdDesc();
    }

    @Override
    @Transactional
    public void blacklistVendor(Long id, String reason) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setStatus("BLACKLISTED");
        vendor.setBlacklistReason(reason);
        vendorRepository.save(vendor);
    }

    @Override
    @Transactional
    public Vendor updateVendor(Long id, VendorRequest request) {
        Vendor vendor = vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));

        vendor.setCompanyName(request.getCompanyName());
        vendor.setTaxId(request.getTaxId());
        vendor.setAddress(request.getAddress());
        vendor.setContactPerson(request.getContactPerson());
        vendor.setEmail(request.getEmail());
        vendor.setPhone(request.getPhone());
        vendor.setPaymentTerms(request.getPaymentTerms());
        vendor.setStartDate(request.getStartDate());
        vendor.setEndDate(request.getEndDate());

        if (request.getSku() != null) vendor.setSku(request.getSku());
        if (request.getUnitPrice() != null) vendor.setUnitPrice(request.getUnitPrice());

        return vendorRepository.save(vendor);
    }

    @Override
    public List<Vendor> getActiveVendors() {
        return vendorRepository.findByStatusOrderByCompanyNameAsc("APPROVED");
    }

    @Override
    public Vendor getVendorById(Long id) {
        return vendorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found with id: " + id));
    }

    @Override
    public List<Vendor> searchActiveVendors(String query) {
        return vendorRepository.findByCompanyNameContainingIgnoreCaseAndStatus(query, "APPROVED");
    }
}