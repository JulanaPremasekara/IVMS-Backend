package org.example.ivms.controller;

import org.example.ivms.dto.SupplierVendorDTO;
import org.example.ivms.dto.VendorRequestDTO;
import org.example.ivms.dto.VendorStatsDTO;
import org.example.ivms.service.SupplierPortalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/supplier-portal")
@CrossOrigin(origins = "*")
public class SupplierPortalBridgeController {

    private final SupplierPortalService supplierPortalService;

    public SupplierPortalBridgeController(SupplierPortalService supplierPortalService) {
        this.supplierPortalService = supplierPortalService;
    }

    @GetMapping("/vendors")
    public ResponseEntity<List<SupplierVendorDTO>> getAllVendors() {
        return ResponseEntity.ok(supplierPortalService.getAllVendors());
    }

    @GetMapping("/vendors/active")
    public ResponseEntity<List<SupplierVendorDTO>> getActiveVendors() {
        return ResponseEntity.ok(supplierPortalService.getActiveVendors());
    }

    @GetMapping("/vendors/{id}")
    public ResponseEntity<SupplierVendorDTO> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierPortalService.getVendorById(id));
    }

    @GetMapping("/vendors/search")
    public ResponseEntity<List<SupplierVendorDTO>> searchVendors(@RequestParam String query) {
        return ResponseEntity.ok(supplierPortalService.searchVendors(query));
    }

    @GetMapping("/vendors/stats")
    public ResponseEntity<VendorStatsDTO> getVendorStats() {
        return ResponseEntity.ok(supplierPortalService.getVendorStats());
    }

    @PostMapping("/vendors/register")
    public ResponseEntity<Object> registerVendor(@RequestBody VendorRequestDTO request) {
        return ResponseEntity.ok(supplierPortalService.registerVendor(request));
    }

    @PatchMapping("/vendors/{id}/approve")
    public ResponseEntity<String> approveVendor(@PathVariable Long id) {
        return ResponseEntity.ok(supplierPortalService.approveVendor(id));
    }

    @PutMapping("/vendors/{id}")
    public ResponseEntity<String> updateVendor(@PathVariable Long id, @RequestBody VendorRequestDTO request) {
        return ResponseEntity.ok(supplierPortalService.updateVendor(id, request));
    }

    @PatchMapping("/vendors/{id}/blacklist")
    public ResponseEntity<String> blacklistVendor(
            @PathVariable Long id,
            @RequestParam String reason) {
        return ResponseEntity.ok(supplierPortalService.blacklistVendor(id, reason));
    }
}