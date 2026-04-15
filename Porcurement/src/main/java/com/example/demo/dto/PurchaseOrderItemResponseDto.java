package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseOrderItemResponseDto {
    private Long inventoryItemId;
    private String name;
    private String sku;
    private Integer quantity;
    private Double price;
}
