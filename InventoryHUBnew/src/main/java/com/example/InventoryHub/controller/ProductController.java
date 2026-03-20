package com.example.InventoryHub.controller;

import com.example.InventoryHub.Config.RabbitMQConfig;
import com.example.InventoryHub.dto.ProductRegistrationDTO;
import com.example.InventoryHub.dto.StockAdjustmentDTO;
import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.service.IProductService;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:3000") // Your React URL
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRegistrationDTO dto) {
        Product savedProduct = productService.registerProduct(dto);
        return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PatchMapping("/{id}/safety-limit")
    public ResponseEntity<String> updateSafetyLimit(@PathVariable Long id, @RequestParam int limit) {
        // 1. Create the message object
        StockAdjustmentDTO message = new StockAdjustmentDTO(id, limit, "SAFETY_STOCK_AUDIT");

        // 2. Send to RabbitMQ
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_NAME, message);

        return ResponseEntity.ok("Limit update sent to AI analysis queue.");
    }
    @GetMapping("/inventory/search")
    public ResponseEntity<List<Product>> searchInventory(@RequestParam String query) {
        // This calls the search logic you likely have in your service
        List<Product> results = productService.searchProducts(query);
        return ResponseEntity.ok(results);
    }
    @PutMapping("/stock/update/{id}")
    public ResponseEntity<String> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        // This calls the service method that saves to DB AND triggers RabbitMQ
        productService.updateStock(id, quantity);
        return ResponseEntity.ok("Stock updated successfully. RabbitMQ event published.");
    }

    // Matches: GET http://localhost:8083/api/v1/products/archived
    @GetMapping("/archived")
    public ResponseEntity<List<Product>> getArchivedProducts() {
        // Fulfills the "Archive Management" view requirements
        List<Product> archived = productService.getArchivedProducts();
        return ResponseEntity.ok(archived);
    }

}
