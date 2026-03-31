package org.example.ivms.dto;

import lombok.Data;

@Data
public class StockTransferRequest {
    private Long productId;
    private Long sourceWarehouseId;
    private Long destinationWarehouseId;
    private Integer quantity;
    private String reason;
    private String notes;
    private boolean requireApproval;
}