package org.example.ivms.controller;

import org.example.ivms.dto.InventoryHubProductDTO;
import org.example.ivms.entity.Warehouse;
import org.example.ivms.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")   // ← Change to specific origins in production!
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @GetMapping("/warehouses")
    public ResponseEntity<List<Warehouse>> getAllWarehouses() {
        List<Warehouse> warehouses = inventoryService.getAllWarehouses();
        return ResponseEntity.ok(warehouses);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductSearchResponse>> searchProducts(
            @RequestParam("q")
            @NotBlank(message = "Search query cannot be empty")
            String query) {

        if (query == null || query.trim().length() < 2) {
            return ResponseEntity.ok(List.of());
        }

        List<InventoryHubProductDTO> products = inventoryService.searchProductsFromInventoryHub(query.trim());

        List<ProductSearchResponse> response = products.stream()
                .map(p -> new ProductSearchResponse(
                        p.getId(),
                        p.getSkuCode(),
                        p.getName(),
                        p.getInitialStock() != null ? p.getInitialStock() : 0
                ))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/stock")
    public ResponseEntity<StockResponse> getStockLevel(
            @RequestParam("product") @Min(1) Long productId,
            @RequestParam("warehouse") @Min(1) Long warehouseId) {

        try {
            int quantity = inventoryService.getStockQuantity(productId, warehouseId);
            return ResponseEntity.ok(new StockResponse(quantity));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new StockResponse(0, "Stock not found or invalid IDs"));
        }
    }

    @PutMapping("/products/{id}/stock")
    public ResponseEntity<String> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(inventoryService.updateStockInInventoryHub(id, quantity));
    }

    public record ProductSearchResponse(
            Long id,
            String sku,
            String name,
            int totalStock
    ) {}

    public record StockResponse(
            int quantity,
            String message
    ) {
        public StockResponse(int quantity) {
            this(quantity, null);
        }
    }

    private int getTotalStockAcrossWarehouses(Long productId) {
        return inventoryService.getTotalStockForProduct(productId);
    }
}