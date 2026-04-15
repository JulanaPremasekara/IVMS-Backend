package org.example.salese_engine.controller;

import org.example.salese_engine.entity.OrderItem;
import org.example.salese_engine.entity.SalesOrder;
import org.example.salese_engine.repository.SalesOrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class SalesOrderController {

    private final SalesOrderRepository repo;

    public SalesOrderController(SalesOrderRepository repo) {
        this.repo = repo;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<List<SalesOrder>> createOrder(@Valid @RequestBody SalesOrderRequest request) {
        // Additional manual validation for items
        if (request.items == null || request.items.isEmpty()) {
            throw new IllegalArgumentException("At least one order item is required");
        }

        double total = 0.0;
        for (OrderItemRequest itemReq : request.items) {
            if (itemReq.productName == null || itemReq.productName.isBlank()) {
                throw new IllegalArgumentException("Product name is required for all items");
            }
            if (itemReq.quantity == null || itemReq.quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for all items");
            }
            if (itemReq.unitPrice == null || itemReq.unitPrice < 0) {
                throw new IllegalArgumentException("Unit price cannot be negative for all items");
            }
        }

        SalesOrder order = new SalesOrder();
        order.setCustomerName(request.customerName);
        order.setCustomerEmail(request.customerEmail);
        order.setShippingAddress(request.shippingAddress);
        order.setShippingCity(request.shippingCity);
        order.setShippingPostalCode(request.shippingPostalCode);
        order.setOrderDate(LocalDate.now());
        order.setStatus("Pending");

        for (OrderItemRequest itemReq : request.items) {
            OrderItem item = new OrderItem();
            item.setProductId(itemReq.productId);
            item.setProductName(itemReq.productName);
            item.setQuantity(itemReq.quantity);
            item.setUnitPrice(itemReq.unitPrice);
            item.setSubtotal(itemReq.quantity * itemReq.unitPrice);
            total += item.getSubtotal();

            item.setSalesOrder(order);
            order.getItems().add(item);
        }
        order.setTotalAmount(total);

        repo.save(order);

        return ResponseEntity.ok(repo.findAll());   // return full list
    }

    // READ
    @GetMapping
    public List<SalesOrder> getAllOrders() {
        return repo.findAll();
    }

    // UPDATE STATUS
    @PutMapping("/{id}/status")
    public ResponseEntity<List<SalesOrder>> updateStatus(@PathVariable Long id, @Valid @RequestBody StatusUpdateRequest request) {
        String status = (request == null || request.getStatus() == null) ? "" : request.getStatus().strip();
        if (status.isEmpty()) {
            throw new IllegalArgumentException("Status is required");
        }

        SalesOrder order = repo.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        repo.save(order);
        return ResponseEntity.ok(repo.findAll());
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<List<SalesOrder>> deleteOrder(@PathVariable Long id) {
        repo.deleteById(id);
        return ResponseEntity.ok(repo.findAll());
    }

    // DTOs
    public static class SalesOrderRequest {
        @NotBlank(message = "Customer name is required")
        public String customerName;

        @Email(message = "Invalid email format")
        public String customerEmail;

        @NotBlank(message = "Shipping address is required")
        public String shippingAddress;

        public String shippingCity;
        public String shippingPostalCode;

        @Valid
        public List<OrderItemRequest> items;
    }

    public static class OrderItemRequest {
        public Long productId;

        @NotBlank(message = "Product name is required")
        public String productName;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        public Integer quantity;

        @NotNull(message = "Unit price is required")
        @DecimalMin(value = "0.0", inclusive = true, message = "Unit price cannot be negative")
        public Double unitPrice;
    }

    public static class StatusUpdateRequest {
        @NotBlank(message = "Status cannot be empty")
        private String status;

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}