package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemResponse {
    private Long id;
    private String skuCode;
    private String name;
    private Integer currentStock;
    private Integer reorderThreshold;
}
