package com.example.demo.dto;

public record ItemDto(
        Long itemId,
        String name,
        String sku,
        int ordered,
        int remaining
) {
}
