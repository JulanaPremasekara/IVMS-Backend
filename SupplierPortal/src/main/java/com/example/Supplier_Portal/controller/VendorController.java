package com.example.Supplier_Portal.controller;

import com.example.Supplier_Portal.dto.VendorRequest;
import com.example.Supplier_Portal.model.Vendor;
import com.example.Supplier_Portal.repository.VendorRepository;
import com.example.Supplier_Portal.service.VendorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;//used to directly interact with the DB
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

//spring annotations and classes for handling http requests
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//to store collections and key-value data
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //handles REST API requests
@RequestMapping("/api/v1/vendors")// base URL for all endpoints in controller
@RequiredArgsConstructor //automatically injects dependencies
//allows frontend to  call backend
@CrossOrigin(
        origins = "http://localhost:3000",
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PATCH, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS}
)

public class VendorController {

    private final VendorService vendorService; //to call business logic methods
    private final VendorRepository vendorRepository; //direct DB access

    @PostMapping("/register")
    public ResponseEntity<?> registerVendor(@Valid @RequestBody VendorRequest request) {
        return new ResponseEntity<>(vendorService.registerVendor(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Vendor>> getAllVendors() {
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
        vendorService.approveVendor(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVendor(@PathVariable Long id, @Valid @RequestBody VendorRequest request) {
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

    @GetMapping("/active")
    public ResponseEntity<List<Vendor>> getActiveVendors() {
        return ResponseEntity.ok(vendorService.getActiveVendors());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> getVendorById(@PathVariable Long id) {
        return ResponseEntity.ok(vendorService.getVendorById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Vendor>> searchVendors(@RequestParam String query) {
        return ResponseEntity.ok(vendorService.searchActiveVendors(query));
    }

    @GetMapping("/{id}/export-pdf")
    public void exportToPDF(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Vendor_Report_VEN" + id + ".pdf";
        response.setHeader(headerKey, headerValue);

        vendorService.generateVendorPdf(id, response);
    }
}