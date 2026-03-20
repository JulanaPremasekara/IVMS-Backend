package com.example.demo.dto;



import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.util.List;

public record PartialReceiptRequest(

        @NotBlank(message = "Order ID is required")
        String orderId,

        @NotBlank(message = "Shipment ID is required")
        String shipmentId,

        @NotBlank(message = "Vendor is required")
        String vendor,

        @NotBlank(message = "Order status is required")
        String orderStatus,

        @NotBlank(message = "Shipment status is required")
        String shipmentStatus,

        @NotNull(message = "Total received is required")
        @PositiveOrZero(message = "Total received cannot be negative")
        Integer totalReceived,

        @NotNull(message = "Total damaged is required")
        @PositiveOrZero(message = "Total damaged cannot be negative")
        Integer totalDamaged,

        @NotEmpty(message = "Items list cannot be empty")
        @Valid
        List<PartialItemRequest> items

) {}
