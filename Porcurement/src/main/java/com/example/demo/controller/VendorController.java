package com.example.demo.controller;

import com.example.demo.dto.VendorDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/vendors")
@CrossOrigin(origins = "http://localhost:3000")
public class VendorController {


    @GetMapping
    public ResponseEntity<List<VendorDTO>> getAllVendors() {

        List<VendorDTO> vendors = List.of(
                new VendorDTO(1L, "TechSupply Inc.", "Active", 4.5, "Net 30", "2027-06-30",List.of("Standard Shipping", "Express (2-Day)", "Overnight")),
                new VendorDTO(2L, "NorthBridge Wholesale", "Active", 4.2, "Net 45", "2026-12-31",List.of("Standard Shipping", "Express (2-Day)")),
                new VendorDTO(3L, "Peak Components", "Active", 4.8, "Net 15", "2028-03-20",List.of("Freight", "Ground Shipping")),
                new VendorDTO(4L, "Global Parts Co.", "Inactive", 3.9, "Prepaid", "2025-09-10",List.of("Freight", "Ground Shipping","Express (2-Day)"))
        );

        return ResponseEntity.ok(vendors);
    }
}
