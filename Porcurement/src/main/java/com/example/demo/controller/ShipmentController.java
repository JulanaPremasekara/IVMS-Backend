package com.example.demo.controller;

import com.example.demo.dto.ActiveShipmentRowDto;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping
    public List<ActiveShipmentRowDto> getShipments() {
        return shipmentService.getActiveShipmentRows();
    }

    @GetMapping("/this-week")
    public List<ActiveShipmentRowDto> getThisWeekShipments() {
        return shipmentService.findByShipmentCommingBetween();
    }
}
