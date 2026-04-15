package com.example.demo.controller;



import com.example.demo.dto.ActiveShipmentRowDto;
import com.example.demo.dto.ArrivalShipmentDto;
import com.example.demo.dto.TimeLineEventsDto;
import com.example.demo.dto.UpdateShipmentStatusRequest;
import com.example.demo.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/incoming")
    public List<ArrivalShipmentDto> getThisWeekShipments() {
        return shipmentService.findByShipmentCommingBetween();
    }

    @GetMapping("/{trackID}/timeline")
    public List<TimeLineEventsDto> getTimeLineShipments(@PathVariable("trackID") String trackingId) {
        return shipmentService.getShipmentTimeLine(trackingId);
    }

    @PutMapping("/{trackingId}/status")
    public ResponseEntity<String> updateShipmentStatus(
            @PathVariable String trackingId,
            @RequestBody UpdateShipmentStatusRequest request
    ) {
        shipmentService.updateShipmentStatus(
                trackingId,
                request.status(),
                request.location(),
                request.note()
        );

        return ResponseEntity.ok("Shipment status updated successfully");
    }
//   @GetMapping("/this-week")
//    public List<ActiveShipmentRowDto> getThisWeekShipments() {
//        return shipmentService.findByShipmentCommingBetween();
//   }
}