//package org.example.ivms.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "products")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Product {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(unique = true, nullable = false, length = 50)
//    private String sku;
//
//    @Column(nullable = false, length = 200)
//    private String name;
//
//    @Column(columnDefinition = "TEXT")
//    private String description;
//
//    @Column(precision = 10, scale = 2)   // ← now valid with BigDecimal
//    private BigDecimal unitPrice;
//
//    private String unitOfMeasure = "pcs";
//
//    @Column(updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    private LocalDateTime updatedAt;
//}