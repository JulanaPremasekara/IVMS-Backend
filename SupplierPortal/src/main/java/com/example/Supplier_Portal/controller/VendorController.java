package com.example.Supplier_Portal.controller;

import com.example.Supplier_Portal.dto.VendorRequest;
import com.example.Supplier_Portal.model.Vendor;
import com.example.Supplier_Portal.repository.VendorRepository;
import com.example.Supplier_Portal.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:5173",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)
public class VendorController {

    private final VendorService vendorService;
    private final VendorRepository vendorRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerVendor(@RequestBody VendorRequest request) {
        // Now returns a vendor initialized with 0% analytics
        return new ResponseEntity<>(vendorService.registerVendor(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
        // Includes the new performanceScore and avgLeadTime fields in the list
        return ResponseEntity.ok(vendorService.getAllVendors());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getVendorStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", vendorRepository.countByStatus("APPROVED"));
        stats.put("pending", vendorRepository.countByStatus("PENDING"));
        stats.put("blacklisted", vendorRepository.countByStatus("BLACKLISTED"));
        return ResponseEntity.ok(stats);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<?> approveVendor(@PathVariable Long id) {
        // Logic in service now ensures restored/approved vendors have baseline metrics
        vendorService.approveVendor(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable Long id, @RequestBody VendorRequest request) {
        vendorService.updateVendor(id, request);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/blacklist")
    public ResponseEntity<?> blacklistVendor(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload) {
        String reason = payload.get("reason");
        vendorService.blacklistVendor(id, reason);
        return ResponseEntity.ok().build();
    }

    // --- ANALYTICS & DIRECTORY ENDPOINTS ---

    @GetMapping("/active")
    public ResponseEntity<List<Vendor>> getActiveVendors() {
        // Returns the list used for the Analytics sidebar
        return ResponseEntity.ok(vendorService.getActiveVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        // Provides full analytics data (performanceScore, totalOrders, etc.) for the profile view
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vendor>> searchVendors(@RequestParam String query) {
        return ResponseEntity.ok(vendorService.searchActiveVendors(query));
    }
}