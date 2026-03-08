package com.example.demo.service;

import com.example.demo.dto.ActiveShipmentRowDto;
import com.example.demo.dto.ArrivalShipmentDto;
import com.example.demo.dto.ItemDto;
import com.example.demo.dto.TimeLineEventsDto;
import com.example.demo.entity.PurchaseOrderItem;
import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShipmentTimeLineEvent;
import com.example.demo.enums.ShipmentStatus;

import com.example.demo.repo.ShipmentRepository;
import com.example.demo.repo.ShipmentTimelineEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentTimelineEventRepository shipmentTimelineEventRepository;

    public List<ActiveShipmentRowDto> getActiveShipmentRows() {
        List<Shipment> shipments = shipmentRepository.findByStatusIn(
                List.of(ShipmentStatus.APPROVED, ShipmentStatus.IN_TRANSIT)
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

    public List<ArrivalShipmentDto> findByShipmentCommingBetween() {
        LocalDate today = LocalDate.now();

        LocalDate start = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate end= today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return shipmentRepository.findByEstimatedArrivalBetween(start,end)
                .stream()
                .map(shipment -> new ArrivalShipmentDto(
                        shipment.getId(),
                        shipment.getPurchaseOrder().getPoNumber(),
                        shipment.getTrackingId(),
                        "Stark Industry",
                        shipment.getEstimatedArrival(),
                        shipment.getStatus().toString(),
                        shipment.getPurchaseOrder().getItems().stream()
                                .map(item->new ItemDto(
                                        item.getInventoryItemId().toString(),
                                        item.getSku(),
                                        item.getQuantity(),
                                        50
                                )).toList()
                )).toList();
    }

    @Transactional
    public void updateShipmentStatus(
            String trackingId,
            ShipmentStatus newStatus,
            String location,
            String note
    ) {
        Shipment shipment = shipmentRepository.findByTrackingId(trackingId)
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(newStatus);

        ShipmentTimeLineEvent event = new ShipmentTimeLineEvent();
        event.setShipment(shipment);
        event.setStatus(newStatus);
        event.setEventTime(LocalDateTime.now());
        event.setLocation(location);
        event.setNote(note);

        shipmentTimelineEventRepository.save(event);
        shipmentRepository.save(shipment);
    }

    public List<TimeLineEventsDto> getShipmentTimeLine(String trackingId) {



        return shipmentTimelineEventRepository.findByShipmentTrackingIdOrderByEventTimeAsc(trackingId)
                .stream()
                .map(event -> new TimeLineEventsDto(
                        event.getStatus().toString(),
                        event.getEventTime(),
                        event.getLocation(),
                        event.getNote(),
                        event.getShipment().getPurchaseOrder().getItems().stream()
                                .map(item->item.getSku())
                                .toList()
                )).toList();
    }


}