package com.example.demo.dto;

import java.time.LocalDate;
import java.util.List;

public record ArrivalShipmentDto(
        Long id,
        String poNumber,
        String trackingNumber,
        String vendor,
        LocalDate dueDate,
        String status,
        List<ItemDto> items
) {}
