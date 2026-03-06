package com.example.demo.controller;

import com.example.demo.dto.CreatePurchaseOrderRequest;
import com.example.demo.dto.PurchaseOrderResponse;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.service.PurchaseOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/purchaseOrders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreatePurchaseOrderRequest req) {
        PurchaseOrder saved = service.create(req);
        return ResponseEntity.ok(Map.of("poNumber", saved.getPoNumber(),"status", saved.getStatus()));
    }

    @GetMapping
    public ResponseEntity<List<PurchaseOrderResponse>> getAll() {
        return ResponseEntity.ok(service.getAllPurchaseOrders());
    }

    @PostMapping("/{id}/approve")
    public PurchaseOrder approve(@PathVariable String id,
                                 @RequestParam String supplierUser) {
        return service.approveBySupplier(id, supplierUser);
    }
}
