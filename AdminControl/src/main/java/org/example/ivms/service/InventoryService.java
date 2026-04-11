package org.example.ivms.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import org.example.ivms.dto.InventoryHubProductDTO;
//import org.example.ivms.entity.Product;
import org.example.ivms.entity.Warehouse;
//import org.example.ivms.repository.ProductRepository;
import org.example.ivms.repository.WarehouseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class InventoryService {

    @Autowired
    private WarehouseRepository warehouseRepository;

   // @Autowired
    //private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebClient inventoryHubWebClient;

    /**
     * Get all active warehouses
     */
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll().stream()
                .filter(Warehouse::isActive)
                .toList();
    }

    /**
     * Search products by SKU or name (case-insensitive partial match) from local DB
     */
//    public List<Product> searchProducts(String query) {
//        if (query == null || query.trim().isEmpty()) {
//            return Collections.emptyList();
//        }
//        String trimmed = query.trim();
//        return productRepository.findBySkuContainingIgnoreCaseOrNameContainingIgnoreCase(trimmed, trimmed);
//    }

    /**
     * Search products from Inventory Hub
     */
    public List<InventoryHubProductDTO> searchProductsFromInventoryHub(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return inventoryHubWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/products/inventory/search")
                        .queryParam("query", query.trim())
                        .build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryHubProductDTO>>() {})
                .block();
    }

    /**
     * Get all products from Inventory Hub
     */
    public List<InventoryHubProductDTO> getAllProductsFromInventoryHub() {
        return inventoryHubWebClient.get()
                .uri("/api/v1/products")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<InventoryHubProductDTO>>() {})
                .block();
    }

    /**
     * Update stock in Inventory Hub
     */
    public String updateStockInInventoryHub(Long productId, Integer quantity) {
        return inventoryHubWebClient.put()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/products/stock/update/{id}")
                        .queryParam("quantity", quantity)
                        .build(productId))
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    /**
     * Get current stock quantity for a specific product in a specific warehouse
     * Returns 0 if warehouse/product not found or stockData is invalid
     */
    public int getStockQuantity(Long productId, Long warehouseId) {
        if (productId == null || warehouseId == null) {
            return 0;
        }

        Optional<Warehouse> warehouseOpt = warehouseRepository.findById(warehouseId);
        if (warehouseOpt.isEmpty()) {
            return 0;
        }

        Warehouse warehouse = warehouseOpt.get();
        String stockData = warehouse.getStockData();

        if (stockData == null || stockData.trim().isEmpty()) {
            return 0;
        }

        try {
            Map<Long, Integer> stockMap = objectMapper.readValue(
                    stockData,
                    new TypeReference<Map<Long, Integer>>() {}
            );
            return stockMap.getOrDefault(productId, 0);
        } catch (Exception e) {
            System.err.println("Failed to parse stock data for warehouse " + warehouseId + ": " + e.getMessage());
            return 0;
        }
    }

    /**
     * Update stock quantity for a product in a warehouse
     * - Positive change = add stock
     * - Negative change = remove stock
     * - Prevents negative quantities
     */
    public void updateStock(Long productId, Long warehouseId, int change) {
        if (productId == null || warehouseId == null) {
            throw new IllegalArgumentException("Product ID and Warehouse ID are required");
        }

        Warehouse warehouse = warehouseRepository.findById(warehouseId)
                .orElseThrow(() -> new IllegalArgumentException("Warehouse not found: " + warehouseId));

        String stockData = warehouse.getStockData();
        Map<Long, Integer> stockMap;

        try {
            stockMap = (stockData != null && !stockData.trim().isEmpty())
                    ? objectMapper.readValue(stockData, new TypeReference<Map<Long, Integer>>() {})
                    : new HashMap<>();
        } catch (Exception e) {
            stockMap = new HashMap<>();
        }

        int current = stockMap.getOrDefault(productId, 0);
        int newQuantity = Math.max(0, current + change);

        stockMap.put(productId, newQuantity);

        try {
            warehouse.setStockData(objectMapper.writeValueAsString(stockMap));
            warehouse.setUpdatedAt(LocalDateTime.now());
            warehouseRepository.save(warehouse);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to update stock for product " + productId + " in warehouse " + warehouseId, e
            );
        }
    }

    /**
     * Get total stock of a product across all local warehouses
     */
    public int getTotalStockForProduct(Long productId) {
        if (productId == null) {
            return 0;
        }

        List<Warehouse> warehouses = warehouseRepository.findAll();
        int total = 0;

        for (Warehouse warehouse : warehouses) {
            total += getStockQuantity(productId, warehouse.getId());
        }

        return total;
    }

    /**
     * Get total stock of a product from Inventory Hub
     */
    public int getTotalStockForProductFromInventoryHub(Long productId) {
        if (productId == null) {
            return 0;
        }

        List<InventoryHubProductDTO> products = getAllProductsFromInventoryHub();
        return products.stream()
                .filter(p -> p.getId().equals(productId))
                .map(p -> p.getInitialStock() == null ? 0 : p.getInitialStock())
                .findFirst()
                .orElse(0);
    }
}