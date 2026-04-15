package com.example.demo.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record PartialItemRequest(

        @NotNull(message = "Item ID is required")
        Long itemId,

        @NotBlank(message = "Item name is required")
        String itemName,

        @NotNull(message = "Ordered quantity is required")
        @PositiveOrZero(message = "Ordered quantity cannot be negative")
        Integer orderedQty,

        @NotNull(message = "Received quantity is required")
        @PositiveOrZero(message = "Received quantity cannot be negative")
        Integer receivedQty,

        @NotNull(message = "Damaged quantity is required")
        @PositiveOrZero(message = "Damaged quantity cannot be negative")
        Integer damagedQty,

        @NotNull(message = "Remaining quantity is required")
        @PositiveOrZero(message = "Remaining quantity cannot be negative")
        Integer remainingQty,

        @NotNull(message = "New order quantity is required")
        @PositiveOrZero(message = "New order quantity cannot be negative")
        Integer newOrderQty

) {}
