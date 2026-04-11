package org.example.ivms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_transfers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "source_warehouse_id", nullable = false)
    private Long sourceWarehouseId;

    @Column(name = "destination_warehouse_id", nullable = false)
    private Long destinationWarehouseId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, length = 100)
    private String reason;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "require_approval")
    private boolean requireApproval = false;

    @Column(nullable = false, length = 50)
    private String status = "Pending";

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    private Long approvedByUserId;
}