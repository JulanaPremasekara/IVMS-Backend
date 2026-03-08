package com.example.InventoryHub.controller;

import com.example.InventoryHub.service.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:3000") // Matches your frontend
public class StockController {

    private final IProductService productService;

    public StockController(IProductService productService) {
        this.productService = productService;
    }

    // PUT request to update stock: /api/stock/update/1?quantity=5
    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok("Stock update process initiated for product ID: " + id);
    }
}
