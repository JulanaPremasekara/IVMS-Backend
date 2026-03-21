package com.example.demo.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Traditional DTO using Lombok for Getters and Setters.
 * Better compatibility with frameworks that rely on the JavaBean naming convention.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRegistrationDTO {

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotBlank(message = "Category is required")
    private String category;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Double length;

    @NotNull(message = "Width is required")
    @Positive(message = "Width must be positive")
    private Double width;

    @NotNull(message = "Height is required")
    @Positive(message = "Height must be positive")
    private Double height;

    
    @Min(value = 0, message = "Power usage cannot be negative")
    private Integer powerUsage;

    private Double weight;

    private String description;

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