package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repo.IProductRepository;
import com.example.demo.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/archive")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ArchiveController {

    private final IProductService productService;
    private final IProductRepository productRepository;

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllArchived() {
        return ResponseEntity.ok(productRepository.findByIsActiveFalse());
    }


    // Matches: GET http://localhost:8083/api/v1/archive/search?query=...
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> results = productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query);
        return ResponseEntity.ok(results);
    }

    // Matches: PATCH http://localhost:8083/api/v1/archive/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<String> archive(@PathVariable Long id) {
        productService.archiveProduct(id);
        return ResponseEntity.ok("Product archived successfully.");
    }

    // Matches: PATCH http://localhost:8083/api/v1/archive/restore/{id}
    @PatchMapping("/restore/{id}")
    public ResponseEntity<String> restoreProduct(@PathVariable Long id) {
        productService.restoreProduct(id);
        return ResponseEntity.ok("Product restored successfully.");
    }
}