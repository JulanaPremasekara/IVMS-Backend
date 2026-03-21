package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    @Column(unique = true, nullable = false,name = "sku_code")
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
    @Column(name = "unit_price")
    private Double unitPrice; // Changed to camelCase for Java standards

    @Column(name = "initial_stock")
    private Integer initialStock; // Matches 'initial_stock' in DB

    @Column(name = "warehouse_location")
    private String warehouseLocation;

    @Column(name = "reorder_threshold") // Match the underscore in DB if it exists
    private Integer reorderThreshold;

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "archive_at")
    private LocalDateTime archivedAt;



    // Getters and Setters ...
}
