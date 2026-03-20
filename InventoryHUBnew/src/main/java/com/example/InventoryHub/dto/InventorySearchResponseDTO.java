package com.example.InventoryHub.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class InventorySearchResponseDTO {
    private String name;
    private String skuCode;
    private Integer totalQuantity;
    private Integer availableToPromise; // (Stock - Reserved)
    private String warehouseLocation;
    private LocalDate nextExpectedShipment;
    private Map<String, Integer> warehouseBreakdown;// Extension 3a: For out of stock items
}