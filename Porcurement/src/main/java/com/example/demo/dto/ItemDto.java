package com.example.demo.dto;

public record ItemDto(
        String name,
        String sku,
        int ordered,
        int remaining
) {
}
