package com.example.InventoryHub.service;

import com.example.InventoryHub.dto.InventorySearchResponseDTO;
import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.repo.IProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InventorySearchService {

    @Autowired
    private IProductRepository productRepository;

    public List<InventorySearchResponseDTO> searchInventory(String query) {
        List<Product> products = productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query);

        // Extension 2a: If no results, you could trigger a "Did you mean" logic here
        if (products.isEmpty()) {
            // return suggestions or empty list
        }

        return products.stream().map(p -> {
            // Logic for ATP: For now, we use initialStock - 0 (until you add Orders)
            int atp = p.getInitialStock();

            return InventorySearchResponseDTO.builder()
                    .name(p.getName())
                    .skuCode(p.getSkuCode())
                    .totalQuantity(p.getInitialStock())
                    .availableToPromise(atp)
                    .warehouseBreakdown(Map.of(p.getWarehouseLocation(), p.getInitialStock()))
                    // Extension 3a: Date would normally come from a Procurement table
                    .nextExpectedShipment(p.getInitialStock() == 0 ? LocalDate.now().plusDays(7) : null)
                    .build();
        }).collect(Collectors.toList());
    }
}
