package com.example.InventoryHub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String skuCode;

    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Technical Specs
    private Double length;
    private Double width;
    private Double height;
    private Integer powerUsage;
    private Double weight;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Pricing & Stock
    private Double unitPrice;
    private Integer initialStock;
    private String warehouseLocation;
    private Integer reorderThreshold;



    // Getters and Setters ...
}
