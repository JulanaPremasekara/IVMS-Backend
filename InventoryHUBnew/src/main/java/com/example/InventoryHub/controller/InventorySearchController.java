package com.example.InventoryHub.controller;

import com.example.InventoryHub.dto.InventorySearchResponseDTO;
import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.repo.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@CrossOrigin(origins = "http://localhost:3000")
public class InventorySearchController {

    @Autowired
    private IProductRepository productRepository;

    @GetMapping("/search")
    public List<InventorySearchResponseDTO> searchInventory(@RequestParam String query) {
        // 1. Search DB for matching products
        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query);
        // 2. Map Entities to DTOs with business logic
        return products.stream().map(p -> {
            // ATP Logic: Currently matching stock, but can be adjusted for pending orders
            int atp = p.getInitialStock() != null ? p.getInitialStock() : 0;

            return InventorySearchResponseDTO.builder()
                    .name(p.getName())
                    .skuCode(p.getSkuCode())
                    .totalQuantity(atp)
                    .availableToPromise(atp)
                    .warehouseLocation(p.getWarehouseLocation())
                    // Extension 3a: If out of stock, set a dummy procurement date
                    .nextExpectedShipment(atp == 0 ? LocalDate.now().plusDays(14) : null)
                    .build();
        }).collect(Collectors.toList());
    }
}