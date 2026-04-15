package com.example.demo.dto;



import java.time.LocalDate;

public record ActiveShipmentRowDto(
        String poNumber,
        String vendorId,
        String items,        // you can change to List later
        Integer qty,
        String origin,
        String destination,
        String status,
        String trackingId,
        LocalDate arrival
) {}
