package com.common.dto;

import java.math.BigDecimal;

public record InventoryItemDto(
        String sku,
        String itemName,
        int currentStock,
        BigDecimal unitPrice
) {}
