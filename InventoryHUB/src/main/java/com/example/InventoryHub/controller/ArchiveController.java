package com.example.InventoryHub.controller;

import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.repo.IProductRepository;
import com.example.InventoryHub.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/archive")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class ArchiveController {

    private final IProductService productService;
    private final IProductRepository productRepository; // For direct search

    // 1. SEARCH ENDPOINT: For the "SKU Selection" bar
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String query,
            @RequestParam(defaultValue = "true") boolean active) {

        // This logic filters based on the "Showing Active" toggle in your React UI
        List<Product> results = productRepository.searchActiveForArchive(query, active);
        return ResponseEntity.ok(results);
    }

    // 2. ACTION ENDPOINT: For the "Discontinue" button
    @PatchMapping("/{id}")
    public ResponseEntity<String> archive(@PathVariable Long id) {
        productService.archiveProduct(id);
        return ResponseEntity.ok("Discontinued successfully.");
    }
}