package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegistrationDTO {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Category is required")
    private String category;

    // --- Technical Specifications made optional ---
    // Removed @NotNull and @Positive
    private Double length;

    private Double width;

    private Double height;

    @Min(value = 0, message = "Power usage cannot be negative")
    private Integer powerUsage;

    private Double weight;

    private String description;

    // --- Critical Fields (Keep these required) ---
    @NotNull(message = "Unit price is required")
    @Positive(message = "Price must be greater than zero")
    private Double unitPrice;

    @NotNull(message = "Initial stock level is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer initialStock;

    @NotBlank(message = "Warehouse location is required")
    private String warehouseLocation;

    private Integer reorderThreshold;
}