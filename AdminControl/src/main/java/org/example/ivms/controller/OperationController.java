package org.example.ivms.controller;

import org.example.ivms.dto.StockTransferRequest;
import org.example.ivms.entity.StockTransfer;
import org.example.ivms.service.StockTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class OperationController {

    @Autowired
    private StockTransferService transferService;

    @PostMapping("/transfers")
    public ResponseEntity<StockTransfer> createTransfer(@Valid @RequestBody StockTransferRequest req) {
        StockTransfer transfer = transferService.createTransfer(req);
        return ResponseEntity.ok(transfer);
    }
}