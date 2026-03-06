package com.example.demo.service;

import com.example.demo.dto.ActiveShipmentRowDto;
import com.example.demo.entity.PurchaseOrderItem;
import com.example.demo.entity.Shipment;
import com.example.demo.enums.ShipmentStatus;

import com.example.demo.repo.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public List<ActiveShipmentRowDto> getActiveShipmentRows() {
        List<Shipment> shipments = shipmentRepository.findByStatusIn(
                List.of(ShipmentStatus.PREPARING, ShipmentStatus.IN_TRANSIT)
        );

        return shipments.stream().map(s -> {
            var po = s.getPurchaseOrder();

            int totalQty = po.getItems() == null ? 0 :
                    po.getItems().stream().mapToInt(PurchaseOrderItem::getQuantity).sum();

            String itemsText = po.getItems() == null ? "" :
                    po.getItems().stream()
                            .map(i -> (i.getSku() != null ? i.getSku() : "Item#" + i.getInventoryItemId())
                                    + " x" + i.getQuantity())
                            .collect(Collectors.joining(", "));

            return new ActiveShipmentRowDto(
                    po.getPoNumber(),
                    po.getVendorId(),
                    itemsText,
                    totalQty,
                    s.getOrigin(),
                    s.getDestination(),
                    s.getStatus().name(),
                    s.getTrackingId(),
                    s.getEstimatedArrival()
            );
        }).toList();
    }

    public List<ActiveShipmentRowDto> findByShipmentCommingBetween() {
        LocalDate today = LocalDate.now();

        LocalDateTime start =today.with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end =today.with(DayOfWeek.SUNDAY).atTime(LocalTime.MAX);

        return shipmentRepository.findByArrivalWeek(start,end)
                .stream()
                .map(shipment -> new ActiveShipmentRowDto(
                        shipment.getPurchaseOrder().getPoNumber()
                ));
    }


}