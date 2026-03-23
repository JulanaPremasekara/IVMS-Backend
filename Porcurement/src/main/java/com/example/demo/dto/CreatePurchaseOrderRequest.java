package com.example.demo.dto;

import com.example.demo.enums.PurchaseOrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record CreatePurchaseOrderRequest(

        @NotNull(message = "Vendor ID is required")
        Long vendorId,

        @NotNull(message = "Expected delivery date is required")
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate expectedDeliveryDate,

        @NotBlank(message = "Shipping method is required")
        String shippingMethod,

        @Size(max = 2000, message = "Internal notes cannot exceed 2000 characters")
        String internalNotes,

        @NotNull(message = "Status is required")
        PurchaseOrderStatus status,

        @NotNull(message = "Total amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
        BigDecimal totalAmount,

        @NotEmpty(message = "At least one item is required")
        @Valid
        List<CreatePurchaseOrderItemRequest> items

) {}
