package org.example.ivms.service;

import org.example.ivms.dto.StockTransferRequest;
import org.example.ivms.entity.StockTransfer;
import org.example.ivms.repository.StockTransferRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class StockTransferService {

    @Autowired
    private StockTransferRepository transferRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private AuditLogService auditLogService;

    public StockTransfer createTransfer(StockTransferRequest req) {
        // Basic validation
        if (req.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (req.getSourceWarehouseId().equals(req.getDestinationWarehouseId())) {
            throw new IllegalArgumentException("Source and destination warehouses cannot be the same");
        }

        // Check available stock
        int available = inventoryService.getStockQuantity(
                req.getProductId(),
                req.getSourceWarehouseId()
        );
        if (req.getQuantity() > available) {
            throw new IllegalStateException(
                    "Insufficient stock in source warehouse. Available: " + available +
                            ", Requested: " + req.getQuantity()
            );
        }

        StockTransfer transfer = new StockTransfer();
        transfer.setProductId(req.getProductId());
        transfer.setSourceWarehouseId(req.getSourceWarehouseId());
        transfer.setDestinationWarehouseId(req.getDestinationWarehouseId());
        transfer.setQuantity(req.getQuantity());
        transfer.setReason(req.getReason());
        transfer.setNotes(req.getNotes());
        transfer.setRequireApproval(req.isRequireApproval());
        transfer.setStatus("Pending");

        StockTransfer saved = transferRepository.save(transfer);

        // Audit log - simplified call (no old/new value, no severity)
        auditLogService.log(
                "Inventory",                    // module
                "CREATE_TRANSFER",              // action
                "StockTransfer",                // entityType
                saved.getId().toString(),       // entityId
                "Normal"                        // status (or "Pending" if you prefer)
        );

        return saved;
    }

    public void approveTransfer(Long transferId, Long approvedByUserId) {
        StockTransfer transfer = transferRepository.findById(transferId)
                .orElseThrow(() -> new IllegalArgumentException("Transfer not found: " + transferId));

        if (!"Pending".equals(transfer.getStatus())) {
            throw new IllegalStateException("Transfer is not in Pending state. Current: " + transfer.getStatus());
        }

        // Update stock: subtract from source, add to destination
        inventoryService.updateStock(
                transfer.getProductId(),
                transfer.getSourceWarehouseId(),
                -transfer.getQuantity()
        );
        inventoryService.updateStock(
                transfer.getProductId(),
                transfer.getDestinationWarehouseId(),
                transfer.getQuantity()
        );

        transfer.setStatus("Completed");
        transfer.setApprovedByUserId(approvedByUserId);
        transfer.setUpdatedAt(LocalDateTime.now());
        transferRepository.save(transfer);

        // Audit log - simplified + pass real user id
        auditLogService.log(
                "Inventory",
                "APPROVE_TRANSFER",
                "StockTransfer",
                transfer.getId().toString(),
                "Normal"
        );

        // Optional: better approach – set current user before logging
        // auditLogService.setCurrentUser(approvedByUserId.toString(), "User-" + approvedByUserId);
        // auditLogService.log("Inventory", "APPROVE_TRANSFER", "StockTransfer", transfer.getId().toString(), "Normal");
    }
}