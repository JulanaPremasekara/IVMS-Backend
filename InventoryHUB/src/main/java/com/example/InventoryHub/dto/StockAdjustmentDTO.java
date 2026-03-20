package com.example.InventoryHub.dto;

import java.io.Serializable;

public record StockAdjustmentDTO(Long productId, int newSafetyLimit, String adjustmentType) implements Serializable {}