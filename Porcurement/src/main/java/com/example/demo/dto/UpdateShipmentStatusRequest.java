package com.example.demo.dto;

import com.example.demo.enums.ShipmentStatus;

public record UpdateShipmentStatusRequest(
        ShipmentStatus status,
        String location,
        String note
) {}
