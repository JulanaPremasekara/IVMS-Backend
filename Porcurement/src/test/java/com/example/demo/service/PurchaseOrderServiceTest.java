package com.example.demo.service;


import com.example.demo.dto.CreatePurchaseOrderItemRequest;
import com.example.demo.dto.CreatePurchaseOrderRequest;
import com.example.demo.dto.PartialItemRequest;
import com.example.demo.dto.PartialReceiptRequest;
import com.example.demo.dto.PurchaseOrderResponse;
import com.example.demo.entity.PartialReceipt;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.PurchaseOrderItem;
import com.example.demo.entity.Shipment;
import com.example.demo.entity.ShipmentTimeLineEvent;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.repo.PartialReceiptItemRepository;
import com.example.demo.repo.PartialReceiptRepository;
import com.example.demo.repo.PurchaseOrderItemRepository;
import com.example.demo.repo.PurchaseOrderRepository;
import com.example.demo.repo.ShipmentRepository;
import com.example.demo.repo.ShipmentTimelineEventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderRepository repo;

    @Mock
    private PartialReceiptRepository partialRepo;

    @Mock
    private PartialReceiptItemRepository partialItemRepo;

    @Mock
    private PurchaseOrderItemRepository purchaseOrderItemRepo;

    @Mock
    private ShipmentRepository shipmentRepo;

    @Mock
    private ShipmentTimelineEventRepository shipmentTimelineEventRepository;

    @InjectMocks
    private PurchaseOrderService service;

    private PurchaseOrder purchaseOrder;
    private PurchaseOrderItem purchaseOrderItem;

    @BeforeEach
    void setUp() {
        purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(1L);
        purchaseOrder.setPoNumber("PO-2026-0001");
        purchaseOrder.setVendorId(10L);
        purchaseOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(5));
        purchaseOrder.setShippingMethod("Air");
        purchaseOrder.setInternalNotes("Urgent");
        purchaseOrder.setStatus(PurchaseOrderStatus.SENT);
        purchaseOrder.setTotalAmount(BigDecimal.valueOf(1000));
        purchaseOrder.setCreatedDate(LocalDateTime.now());
        purchaseOrder.setCreatedBy("SYSTEM_USER");

        purchaseOrderItem = new PurchaseOrderItem();
        purchaseOrderItem.setId(101L);
        purchaseOrderItem.setInventoryItemId(1001L);
        purchaseOrderItem.setSku("SKU-001");
        purchaseOrderItem.setQuantity(10);
        purchaseOrderItem.setPrice(BigDecimal.valueOf(50));
        purchaseOrderItem.setReceivedQty(0);
        purchaseOrderItem.setDamagedQty(0);
        purchaseOrderItem.setRemainingQty(10);

        purchaseOrder.setItems(List.of(purchaseOrderItem));
    }

    @Nested
    @DisplayName("create() tests")
    class CreateTests {

        @Test
        @DisplayName("should create purchase order successfully")
        void shouldCreatePurchaseOrderSuccessfully() {
            CreatePurchaseOrderItemRequest itemRequest =
                    new CreatePurchaseOrderItemRequest(1001L, "SKU-001", 10, BigDecimal.valueOf(50));

            CreatePurchaseOrderRequest request =
                    new CreatePurchaseOrderRequest(
                            10L,
                            LocalDate.now().plusDays(5),
                            "Air",
                            "Urgent",
                            PurchaseOrderStatus.DRAFT,
                            BigDecimal.valueOf(500),
                            List.of(itemRequest)
                    );

            when(repo.countByCreatedDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(0L);

            when(repo.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            PurchaseOrder result = service.create(request);

            assertNotNull(result);
            assertEquals(10L, result.getVendorId());
            assertEquals("Air", result.getShippingMethod());
            assertEquals("Urgent", result.getInternalNotes());
            assertEquals(PurchaseOrderStatus.DRAFT, result.getStatus());
            assertEquals(BigDecimal.valueOf(500), result.getTotalAmount());
            assertNotNull(result.getPoNumber());
            assertEquals(1, result.getItems().size());

            PurchaseOrderItem savedItem = result.getItems().get(0);
            assertEquals(1001L, savedItem.getInventoryItemId());
            assertEquals("SKU-001", savedItem.getSku());
            assertEquals(10, savedItem.getQuantity());
            assertEquals(0, savedItem.getReceivedQty());
            assertEquals(0, savedItem.getDamagedQty());
            assertEquals(10, savedItem.getRemainingQty());

            verify(repo).save(any(PurchaseOrder.class));
        }
    }

    @Nested
    @DisplayName("getAllPurchaseOrders() tests")
    class GetAllPurchaseOrdersTests {

        @Test
        @DisplayName("should return mapped purchase order responses")
        void shouldReturnMappedPurchaseOrderResponses() {
            when(repo.findAllWithItemsOrderByPoNumberDesc()).thenReturn(List.of(purchaseOrder));

            List<PurchaseOrderResponse> responses = service.getAllPurchaseOrders();

            assertNotNull(responses);
            assertEquals(1, responses.size());

            PurchaseOrderResponse response = responses.get(0);
            assertEquals(purchaseOrder.getPoNumber(), response.id());
            assertEquals("TechSupply Inc.", response.vendor());
            assertEquals(purchaseOrder.getStatus().name(), response.status());
            assertEquals(purchaseOrder.getTotalAmount(), response.total());
            assertEquals(purchaseOrder.getCreatedBy(), response.creator());
            assertEquals(List.of("SKU-001"), response.items());

            verify(repo).findAllWithItemsOrderByPoNumberDesc();
        }
    }

    @Nested
    @DisplayName("approveBySupplier() tests")
    class ApproveBySupplierTests {

        @Test
        @DisplayName("should approve purchase order and create shipment when shipment does not exist")
        void shouldApprovePurchaseOrderAndCreateShipment() {
            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(repo.countByCreatedDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(0L);

            when(shipmentRepo.findByTrackingId(anyString())).thenAnswer(invocation -> {
                String trackingId = invocation.getArgument(0);

                Shipment shipment = Shipment.builder()
                        .vendorId(purchaseOrder.getVendorId())
                        .origin("TBD")
                        .destination("TBD")
                        .status(ShipmentStatus.APPROVED)
                        .trackingId(trackingId)
                        .estimatedArrival(purchaseOrder.getExpectedDeliveryDate())
                        .createdDate(LocalDateTime.now())
                        .build();

                shipment.setPurchaseOrder(purchaseOrder);
                return Optional.of(shipment);
            });
            when(repo.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(shipmentTimelineEventRepository.save(any(ShipmentTimeLineEvent.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            PurchaseOrder result = service.approveBySupplier("PO-2026-0001", "supplierUser");

            assertNotNull(result);
            assertEquals(PurchaseOrderStatus.APPROVED, result.getStatus());
            verify(repo).save(any(PurchaseOrder.class));
            verify(shipmentRepo).save(any(Shipment.class));
            verify(shipmentTimelineEventRepository).save(any(ShipmentTimeLineEvent.class));
        }

        @Test
        @DisplayName("should throw exception when purchase order not found")
        void shouldThrowWhenPurchaseOrderNotFound() {
            when(repo.findByPoNumber("PO-404")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.approveBySupplier("PO-404", "supplierUser")
            );

            assertEquals("PO not found: PO-404", ex.getMessage());
            verify(repo, never()).save(any());
        }

        @Test
        @DisplayName("should throw exception when purchase order status is not SENT")
        void shouldThrowWhenPurchaseOrderNotSent() {
            purchaseOrder.setStatus(PurchaseOrderStatus.DRAFT);
            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));

            IllegalStateException ex = assertThrows(
                    IllegalStateException.class,
                    () -> service.approveBySupplier("PO-2026-0001", "supplierUser")
            );

            assertEquals("Only SENT purchase orders can be approved.", ex.getMessage());
            verify(repo, never()).save(any());
        }
    }

    @Nested
    @DisplayName("get() tests")
    class GetTests {

        @Test
        @DisplayName("should return purchase order by id")
        void shouldReturnPurchaseOrderById() {
            when(repo.findById(1L)).thenReturn(Optional.of(purchaseOrder));

            PurchaseOrder result = service.get(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("PO-2026-0001", result.getPoNumber());
        }

        @Test
        @DisplayName("should throw exception when purchase order not found by id")
        void shouldThrowWhenPurchaseOrderNotFoundById() {
            when(repo.findById(99L)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.get(99L)
            );

            assertEquals("PO not found: 99", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("savePartialReceipt() tests")
    class SavePartialReceiptTests {

        @Test
        @DisplayName("should save partial receipt and create new reorder PO when newOrderQty > 0")
        void shouldSavePartialReceiptAndCreateReorderPO() {
            Shipment shipment = Shipment.builder()
                    .trackingId("TRC-2026-0001")
                    .status(ShipmentStatus.APPROVED)
                    .origin("Warehouse A")
                    .destination("Store B")
                    .createdDate(LocalDateTime.now())
                    .vendorId(10L)
                    .estimatedArrival(LocalDate.now().plusDays(3))
                    .build();
            shipment.setPurchaseOrder(purchaseOrder);
            purchaseOrder.setShipment(shipment);

            PartialItemRequest itemRequest = new PartialItemRequest(
                    101L,
                    "Item 1",
                    10,
                    6,
                    1,
                    3,
                    4
            );

            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-2026-0001",
                    "TRC-2026-0001",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    6,
                    1,
                    List.of(itemRequest)
            );

            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(shipmentRepo.findByTrackingId("TRC-2026-0001")).thenReturn(Optional.of(shipment));
            when(purchaseOrderItemRepo.findByIdAndPurchaseOrderId(101L, 1L))
                    .thenReturn(Optional.of(purchaseOrderItem));
            when(repo.countByCreatedDateBetween(any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(1L);

            when(repo.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(partialRepo.save(any(PartialReceipt.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(partialItemRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> service.savePartialReceipt(request));

            verify(repo, atLeast(1)).save(any(PurchaseOrder.class));
            verify(shipmentRepo).save(shipment);
            verify(partialRepo).save(any(PartialReceipt.class));
            verify(partialItemRepo).saveAll(anyList());
        }

        @Test
        @DisplayName("should save partial receipt without creating new PO when newOrderQty is zero")
        void shouldSavePartialReceiptWithoutCreatingReorderPO() {
            Shipment shipment = Shipment.builder()
                    .trackingId("TRC-2026-0001")
                    .status(ShipmentStatus.APPROVED)
                    .origin("Warehouse A")
                    .destination("Store B")
                    .createdDate(LocalDateTime.now())
                    .vendorId(10L)
                    .estimatedArrival(LocalDate.now().plusDays(3))
                    .build();
            shipment.setPurchaseOrder(purchaseOrder);
            purchaseOrder.setShipment(shipment);

            PartialItemRequest itemRequest = new PartialItemRequest(
                    101L,
                    "Item 1",
                    10,
                    10,
                    0,
                    0,
                    0
            );

            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-2026-0001",
                    "TRC-2026-0001",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    10,
                    0,
                    List.of(itemRequest)
            );

            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(shipmentRepo.findByTrackingId("TRC-2026-0001")).thenReturn(Optional.of(shipment));
            when(purchaseOrderItemRepo.findByIdAndPurchaseOrderId(101L, 1L))
                    .thenReturn(Optional.of(purchaseOrderItem));
            when(repo.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(shipmentRepo.save(any(Shipment.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(partialRepo.save(any(PartialReceipt.class))).thenAnswer(invocation -> invocation.getArgument(0));
            when(partialItemRepo.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() -> service.savePartialReceipt(request));

            verify(partialRepo).save(any(PartialReceipt.class));
            verify(partialItemRepo).saveAll(anyList());
        }

        @Test
        @DisplayName("should throw exception when original purchase order is not found")
        void shouldThrowWhenOriginalPurchaseOrderNotFound() {
            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-404",
                    "TRC-2026-0001",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    5,
                    0,
                    List.of(new PartialItemRequest(101L, "Item 1", 10, 5, 0, 5, 0))
            );

            when(repo.findByPoNumber("PO-404")).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.savePartialReceipt(request)
            );

            assertEquals("PO not found: PO-404", ex.getMessage());
        }

        @Test
        @DisplayName("should throw exception when shipment is not found")
        void shouldThrowWhenShipmentNotFound() {
            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-2026-0001",
                    "TRC-404",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    5,
                    0,
                    List.of(new PartialItemRequest(101L, "Item 1", 10, 5, 0, 5, 0))
            );

            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(shipmentRepo.findByTrackingId("TRC-404")).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(
                    EntityNotFoundException.class,
                    () -> service.savePartialReceipt(request)
            );

            assertEquals("Shipment not found: TRC-404", ex.getMessage());
        }

        @Test
        @DisplayName("should throw exception when shipment does not belong to purchase order")
        void shouldThrowWhenShipmentDoesNotBelongToOrder() {
            PurchaseOrder anotherPo = new PurchaseOrder();
            anotherPo.setId(999L);

            Shipment shipment = Shipment.builder()
                    .trackingId("TRC-2026-0001")
                    .build();
            shipment.setPurchaseOrder(anotherPo);

            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-2026-0001",
                    "TRC-2026-0001",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    5,
                    0,
                    List.of(new PartialItemRequest(101L, "Item 1", 10, 5, 0, 5, 0))
            );

            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(shipmentRepo.findByTrackingId("TRC-2026-0001")).thenReturn(Optional.of(shipment));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.savePartialReceipt(request)
            );

            assertEquals("Shipment does not belong to the provided purchase order", ex.getMessage());
        }

        @Test
        @DisplayName("should throw exception for invalid partial item quantities")
        void shouldThrowForInvalidPartialItemQuantities() {
            Shipment shipment = Shipment.builder()
                    .trackingId("TRC-2026-0001")
                    .build();
            shipment.setPurchaseOrder(purchaseOrder);

            PartialItemRequest invalidItem = new PartialItemRequest(
                    101L,
                    "Item 1",
                    10,
                    8,
                    5,
                    -3,
                    2
            );

            PartialReceiptRequest request = new PartialReceiptRequest(
                    "PO-2026-0001",
                    "TRC-2026-0001",
                    "TechSupply Inc.",
                    "APPROVED",
                    "APPROVED",
                    8,
                    5,
                    List.of(invalidItem)
            );

            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(shipmentRepo.findByTrackingId("TRC-2026-0001")).thenReturn(Optional.of(shipment));
            when(purchaseOrderItemRepo.findByIdAndPurchaseOrderId(101L, 1L))
                    .thenReturn(Optional.of(purchaseOrderItem));

            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.savePartialReceipt(request)
            );

            assertTrue(ex.getMessage().contains("negative"));
        }
    }

    @Nested
    @DisplayName("voidPurchaseOrder() tests")
    class VoidPurchaseOrderTests {

        @Test
        @DisplayName("should void purchase order successfully")
        void shouldVoidPurchaseOrderSuccessfully() {
            when(repo.findByPoNumber("PO-2026-0001")).thenReturn(Optional.of(purchaseOrder));
            when(repo.save(any(PurchaseOrder.class))).thenAnswer(invocation -> invocation.getArgument(0));

            assertDoesNotThrow(() ->
                    service.voidPurchaseOrder("PO-2026-0001", "Vendor cannot fulfill order", true)
            );

            assertEquals(PurchaseOrderStatus.VOIDED, purchaseOrder.getStatus());
            assertEquals("Vendor cannot fulfill order", purchaseOrder.getInternalNotes());
            verify(repo).save(purchaseOrder);
        }

        @Test
        @DisplayName("should throw exception when reason is null")
        void shouldThrowWhenReasonIsNull() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.voidPurchaseOrder("PO-2026-0001", null, true)
            );

            assertEquals("Reason is required", ex.getMessage());
        }

        @Test
        @DisplayName("should throw exception when reason is blank")
        void shouldThrowWhenReasonIsBlank() {
            IllegalArgumentException ex = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.voidPurchaseOrder("PO-2026-0001", "   ", true)
            );

            assertEquals("Reason is required", ex.getMessage());
        }
    }
}