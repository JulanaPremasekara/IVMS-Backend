package com.example.demo.dto;



import jakarta.validation.constraints.*;


import java.math.BigDecimal;

public record CreatePurchaseOrderItemRequest(

        @NotNull(message = "Inventory item ID is required")
        Long inventoryItemId,

        @NotBlank(message = "SKU is required")
        @Size(max = 100, message = "SKU must not exceed 100 characters")
        String sku,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 12, fraction = 2, message = "Price format is invalid")
        BigDecimal price

) {}
