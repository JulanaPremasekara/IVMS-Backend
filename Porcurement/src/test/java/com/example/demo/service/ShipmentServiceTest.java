package com.example.demo.service;

import com.example.demo.dto.ArrivalShipmentDto;
import com.example.demo.dto.ItemDto;
import com.example.demo.dto.TimeLineEventsDto;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.PurchaseOrderItem;
import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShipmentTimeLineEvent;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.repo.ShipmentRepository;
import com.example.demo.repo.ShipmentTimelineEventRepository;
import com.example.demo.service.ShipmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;

    @Mock
    private ShipmentTimelineEventRepository shipmentTimelineEventRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    @Test
    void findByShipmentCommingBetween_shouldReturnMappedArrivalShipments() {
        // Arrange
        PurchaseOrderItem item1 = new PurchaseOrderItem();
        item1.setInventoryItemId(101L);
        item1.setSku("SKU-101");
        item1.setQuantity(5);

        PurchaseOrderItem item2 = new PurchaseOrderItem();
        item2.setInventoryItemId(102L);
        item2.setSku("SKU-102");
        item2.setQuantity(3);

        PurchaseOrder po = new PurchaseOrder();
        po.setPoNumber("PO-1001");
        po.setItems(List.of(item1, item2));

        Shipment shipment = new Shipment();
        shipment.setId(1L);
        shipment.setPurchaseOrder(po);
        shipment.setTrackingId("TRK-123");
        shipment.setEstimatedArrival(LocalDate.of(2026, 4, 5));
        shipment.setStatus(ShipmentStatus.APPROVED);

        when(shipmentRepository.findByEstimatedArrivalBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(shipment));

        // Act
        List<ArrivalShipmentDto> result = shipmentService.findByShipmentCommingBetween();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        ArrivalShipmentDto dto = result.get(0);

        assertEquals(1L, dto.id());
        assertEquals("PO-1001", dto.poNumber());
        assertEquals("TRK-123", dto.trackingNumber());
        assertEquals("Stark Industry", dto.vendor());
        assertEquals(LocalDate.of(2026, 4, 5), dto.dueDate());
        assertEquals("APPROVED", dto.status());

        assertEquals(2, dto.items().size());

        ItemDto firstItem = dto.items().get(0);
        assertEquals("101", firstItem.name());
        assertEquals("SKU-101", firstItem.sku());
        assertEquals(5, firstItem.ordered());
        assertEquals(50, firstItem.remaining());

        ItemDto secondItem = dto.items().get(1);
        assertEquals("102", secondItem.name());
        assertEquals("SKU-102", secondItem.sku());
        assertEquals(3, secondItem.ordered());
        assertEquals(50, secondItem.remaining());
    }

    @Test
    void findByShipmentCommingBetween_shouldReturnEmptyList_whenRepositoryReturnsEmpty() {
        // Arrange
        when(shipmentRepository.findByEstimatedArrivalBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of());

        // Act
        List<ArrivalShipmentDto> result = shipmentService.findByShipmentCommingBetween();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void updateShipmentStatus_shouldUpdateShipmentAndSaveTimelineEvent() {
        // Arrange
        Shipment shipment = new Shipment();
        shipment.setTrackingId("TRK-999");
        shipment.setStatus(ShipmentStatus.APPROVED);

        when(shipmentRepository.findByTrackingId("TRK-999"))
                .thenReturn(Optional.of(shipment));

        // Act
        shipmentService.updateShipmentStatus(
                "TRK-999",
                ShipmentStatus.IN_TRANSIT,
                "Chennai",
                "Shipment moved to next hub"
        );

        // Assert
        assertEquals(ShipmentStatus.IN_TRANSIT, shipment.getStatus());
        verify(shipmentRepository).save(shipment);
        verify(shipmentTimelineEventRepository).save(any(ShipmentTimeLineEvent.class));
    }

    @Test
    void updateShipmentStatus_shouldSaveCorrectTimelineEventDetails() {
        // Arrange
        Shipment shipment = new Shipment();
        shipment.setTrackingId("TRK-777");
        shipment.setStatus(ShipmentStatus.APPROVED);

        when(shipmentRepository.findByTrackingId("TRK-777"))
                .thenReturn(Optional.of(shipment));

        ArgumentCaptor<ShipmentTimeLineEvent> eventCaptor =
                ArgumentCaptor.forClass(ShipmentTimeLineEvent.class);

        // Act
        shipmentService.updateShipmentStatus(
                "TRK-777",
                ShipmentStatus.DELIVERED,
                "Mumbai",
                "Delivered successfully"
        );

        // Assert
        verify(shipmentTimelineEventRepository).save(eventCaptor.capture());
        verify(shipmentRepository).save(shipment);

        ShipmentTimeLineEvent savedEvent = eventCaptor.getValue();

        assertEquals(ShipmentStatus.DELIVERED, savedEvent.getStatus());
        assertEquals("Mumbai", savedEvent.getLocation());
        assertEquals("Delivered successfully", savedEvent.getNote());
        assertEquals(shipment, savedEvent.getShipment());
        assertNotNull(savedEvent.getEventTime());
    }

    @Test
    void updateShipmentStatus_shouldThrowException_whenShipmentNotFound() {
        // Arrange
        when(shipmentRepository.findByTrackingId("BAD-ID"))
                .thenReturn(Optional.empty());

        // Act + Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                shipmentService.updateShipmentStatus(
                        "BAD-ID",
                        ShipmentStatus.DELIVERED,
                        "Hyderabad",
                        "Delivered"
                )
        );

        assertEquals("Shipment not found", exception.getMessage());
        verify(shipmentTimelineEventRepository, never()).save(any());
        verify(shipmentRepository, never()).save(any());
    }

    @Test
    void getShipmentTimeLine_shouldReturnMappedTimelineEvents() {
        // Arrange
        PurchaseOrderItem item1 = new PurchaseOrderItem();
        item1.setSku("SKU-101");

        PurchaseOrderItem item2 = new PurchaseOrderItem();
        item2.setSku("SKU-102");

        PurchaseOrder po = new PurchaseOrder();
        po.setItems(List.of(item1, item2));

        Shipment shipment = new Shipment();
        shipment.setPurchaseOrder(po);
        shipment.setTrackingId("TRK-555");

        ShipmentTimeLineEvent event = new ShipmentTimeLineEvent();
        event.setShipment(shipment);
        event.setStatus(ShipmentStatus.IN_TRANSIT);
        event.setEventTime(LocalDateTime.of(2026, 4, 1, 10, 30));
        event.setLocation("Bangalore");
        event.setNote("Reached sorting center");

        when(shipmentTimelineEventRepository.findByShipmentTrackingIdOrderByEventTimeAsc("TRK-555"))
                .thenReturn(List.of(event));

        // Act
        List<TimeLineEventsDto> result = shipmentService.getShipmentTimeLine("TRK-555");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());

        TimeLineEventsDto dto = result.get(0);
        assertEquals("IN_TRANSIT", dto.status());
        assertEquals(LocalDateTime.of(2026, 4, 1, 10, 30), dto.time());
        assertEquals("Bangalore", dto.Location());
        assertEquals("Reached sorting center", dto.note());
        assertEquals(List.of("SKU-101", "SKU-102"), dto.items());
    }

    @Test
    void getShipmentTimeLine_shouldReturnEmptyList_whenNoEventsFound() {
        // Arrange
        when(shipmentTimelineEventRepository.findByShipmentTrackingIdOrderByEventTimeAsc("TRK-EMPTY"))
                .thenReturn(List.of());

        // Act
        List<TimeLineEventsDto> result = shipmentService.getShipmentTimeLine("TRK-EMPTY");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}