package com.example.InventoryHub.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProductSummaryDTO {
    private Long id;
    private String skuCode;
    private String name;
    private Integer currentStock;
    private boolean hasPendingOrders;
    private boolean isActive;
}