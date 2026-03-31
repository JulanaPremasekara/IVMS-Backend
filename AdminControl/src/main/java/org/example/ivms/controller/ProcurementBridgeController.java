package org.example.ivms.controller;

import org.example.ivms.dto.ProcurementIncomingShipmentDTO;
import org.example.ivms.dto.ProcurementPurchaseOrderDTO;
import org.example.ivms.dto.ProcurementShipmentDTO;
import org.example.ivms.service.ProcurementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/procurement")
@CrossOrigin(origins = "*")
public class ProcurementBridgeController {

    private final ProcurementService procurementService;

    public ProcurementBridgeController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @GetMapping("/purchase-orders")
    public ResponseEntity<List<ProcurementPurchaseOrderDTO>> getPurchaseOrders() {
        return ResponseEntity.ok(procurementService.getAllPurchaseOrders());
    }

    @GetMapping("/shipments")
    public ResponseEntity<List<ProcurementShipmentDTO>> getShipments() {
        return ResponseEntity.ok(procurementService.getAllShipments());
    }

    @GetMapping("/shipments/incoming")
    public ResponseEntity<List<ProcurementIncomingShipmentDTO>> getIncomingShipments() {
        return ResponseEntity.ok(procurementService.getIncomingShipments());
    }

    @PostMapping("/purchase-orders/{poNumber}/approve")
    public ResponseEntity<String> approvePurchaseOrder(
            @PathVariable String poNumber,
            @RequestParam String supplierUser) {
        return ResponseEntity.ok(procurementService.approvePurchaseOrder(poNumber, supplierUser));
    }
}