package com.example.demo.controller;

import com.example.demo.dto.InventoryItemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventoryController {

    @GetMapping("/items")
    public ResponseEntity<List<InventoryItemDTO>> getAllItems() {
        List<InventoryItemDTO> items = List.of(
                new InventoryItemDTO(1L, "Premium Wireless Headphones", "SKU-001", 25, 50, 100, 85.0),
                new InventoryItemDTO(2L, "Smart Watch Series 5", "SKU-003", 15, 30, 75, 299.0),
                new InventoryItemDTO(3L, "Bluetooth Speaker", "SKU-004", 8, 25, 50, 45.0),
                new InventoryItemDTO(4L, "Gaming Mouse Pro", "SKU-005", 40, 60, 120, 59.99)
        );
        return ResponseEntity.ok(items);
    }
}
