package com.example.Supplier_Portal.service;

import com.example.Supplier_Portal.dto.VendorRequest;
import com.example.Supplier_Portal.model.Vendor;
import java.util.List;
import java.util.Map;

public interface VendorService {
    // Standard Registration
    Map<String, Object> registerVendor(VendorRequest request);

    // Management Methods
    List<Vendor> getAllVendors();
    void approveVendor(Long id);

    // UPDATED: Now requires reason as per Use Case 18
    void blacklistVendor(Long id, String reason);

    // Update Method (returns the updated object for the UI)
    Vendor updateVendor(Long id, VendorRequest request);

    // Directory Methods
    Vendor getVendorById(Long id);
    List<Vendor> getActiveVendors();
    List<Vendor> searchActiveVendors(String query);
}