package com.example.demo.service;

import com.example.demo.config.InventoryClient;
import com.example.demo.config.VendorClient;
import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repo;
    private final PartialReceiptRepository partialRepo;
    private final PartialReceiptItemRepository partialItemRepo;
    private final PurchaseOrderItemRepository purchaseOrderItemRepo;
    private final ShipmentRepository shipmentRepo;
    private final ShipmentTimelineEventRepository shipmentTimelineEventRepository;
    private final InventoryClient inventoryClient;
    private final VendorClient vendorClient;

    public PurchaseOrderService(
            PurchaseOrderRepository repo,
            PurchaseOrderItemRepository purchaseOrderItemRepo,
            PartialReceiptRepository partialRepo,
            PartialReceiptItemRepository partialItemRepo,
            ShipmentRepository shipmentRepo,
            ShipmentTimelineEventRepository shipmentTimelineEventRepository,
            InventoryClient inventoryClient,
            VendorClient vendorClient) {
        this.repo = repo;
        this.purchaseOrderItemRepo = purchaseOrderItemRepo;
        this.partialRepo = partialRepo;
        this.partialItemRepo = partialItemRepo;
        this.shipmentRepo = shipmentRepo;
        this.shipmentTimelineEventRepository = shipmentTimelineEventRepository;
        this.inventoryClient = inventoryClient;
        this.vendorClient = vendorClient;
    }

    @Transactional
    public PurchaseOrder create(CreatePurchaseOrderRequest req) {
        PurchaseOrder po = new PurchaseOrder();
        po.setVendorId(req.vendorId());
        po.setExpectedDeliveryDate(req.expectedDeliveryDate());
        po.setShippingMethod(req.shippingMethod());
        po.setInternalNotes(req.internalNotes());
        po.setStatus(req.status());
        po.setPoNumber(generatePoNumber());
        po.setTotalAmount(req.totalAmount());
        po.setCreatedDate(LocalDateTime.now());
        po.setCreatedBy("SYSTEM_USER");

        List<PurchaseOrderItem> poItems = req.items().stream().map(i -> {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setInventoryItemId(i.inventoryItemId());
            item.setSku(i.sku());
            item.setQuantity(i.quantity());
            item.setPrice(i.price());
            item.setReceivedQty(0);
            item.setDamagedQty(0);
            item.setRemainingQty(i.quantity());
            item.setPurchaseOrder(po);
            return item;
        }).toList();

        po.setItems(poItems);

        return repo.save(po);
    }

    private String generatePoNumber() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long todayCount = repo.countByCreatedDateBetween(startOfDay, endOfDay);
        long nextNumber = todayCount + 1;

        return String.format("PO-%s-%04d", today, nextNumber);
    }

    public List<PurchaseOrderResponse> mapToResponseList(List<PurchaseOrder> purchaseOrders) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return purchaseOrders.stream()
                .map(po -> {
                    VendorResponse vendor = vendorClient.getVendor(po.getVendorId());

                    return new PurchaseOrderResponse(
                            po.getPoNumber(),
                            vendor != null ? vendor.getCompanyName() : "Unknown Vendor",
                            po.getStatus().name(),
                            po.getCreatedDate().format(formatter),
                            po.getExpectedDeliveryDate() != null
                                    ? po.getExpectedDeliveryDate().format(formatter)
                                    : null,
                            po.getTotalAmount(),
                            po.getCreatedBy(),
                            po.getItems().stream()
                                    .map(PurchaseOrderItem::getSku)
                                    .toList()
                    );
                })
                .toList();
    }
    public List<PurchaseOrderResponse> getAllPurchaseOrders() {
        List<PurchaseOrder> purchaseOrders = repo.findAllWithItemsOrderByPoNumberDesc();
        return mapToResponseList(purchaseOrders);
    }

    @Transactional
    public PurchaseOrder approveBySupplier(String poId, String supplierUser) {
        PurchaseOrder po = repo.findByPoNumber(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));

        if (po.getStatus() != PurchaseOrderStatus.SENT) {
            throw new IllegalStateException("Only SENT purchase orders can be approved.");
        }

        po.setStatus(PurchaseOrderStatus.APPROVED);

        if (po.getShipment() == null) {
            Shipment shipment = Shipment.builder()
                    .vendorId(po.getVendorId())
                    .origin("TBD")
                    .destination("TBD")
                    .status(ShipmentStatus.APPROVED)
                    .trackingId(generateTrackingNumber())
                    .estimatedArrival(po.getExpectedDeliveryDate())
                    .createdDate(LocalDateTime.now())
                    .build();

            po.attachShipment(shipment);
        }

        Shipment shipment = shipmentRepo.findByTrackingId(po.getShipment().getTrackingId())
                .orElseThrow(() -> new RuntimeException("Shipment not found"));

        shipment.setStatus(ShipmentStatus.APPROVED);

        ShipmentTimeLineEvent event = new ShipmentTimeLineEvent();
        event.setShipment(shipment);
        event.setStatus(ShipmentStatus.APPROVED);
        event.setEventTime(LocalDateTime.now());
        event.setLocation(shipment.getOrigin());
        event.setNote("Vendor Accept the Order");

        shipmentTimelineEventRepository.save(event);
        shipmentRepo.save(shipment);
        return repo.save(po);
    }

    public PurchaseOrder get(Long poId) {
        return repo.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));
    }

    private String generateTrackingNumber() {
        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long todayCount = repo.countByCreatedDateBetween(startOfDay, endOfDay);
        long nextNumber = todayCount + 1;

        return String.format("TRC-%s-%04d", today, nextNumber);
    }

    @Transactional
    public void savePartialReceipt(PartialReceiptRequest request) {
        PurchaseOrder originalOrder = repo.findByPoNumber(request.orderId())
                .orElseThrow(() -> new EntityNotFoundException("PO not found: " + request.orderId()));

        Shipment shipment = shipmentRepo.findByTrackingId(request.shipmentId())
                .orElseThrow(() -> new EntityNotFoundException("Shipment not found: " + request.shipmentId()));

        validateShipmentBelongsToOrder(originalOrder, shipment);

        originalOrder.setStatus(PurchaseOrderStatus.RECEIVED);
        shipment.setStatus(ShipmentStatus.DELIVERED);

        PurchaseOrder newOrder = new PurchaseOrder();
        newOrder.setPoNumber(generatePoNumber());
        newOrder.setVendorId(originalOrder.getVendorId());
        newOrder.setExpectedDeliveryDate(LocalDate.now().plusDays(7));
        newOrder.setShippingMethod(originalOrder.getShippingMethod());
        newOrder.setInternalNotes("Auto-created from partial receipt of " + originalOrder.getPoNumber());
        newOrder.setStatus(PurchaseOrderStatus.DRAFT);
        newOrder.setTotalAmount(BigDecimal.ZERO);
        newOrder.setCreatedDate(LocalDateTime.now());
        newOrder.setCreatedBy("SYSTEM_USER");

        BigDecimal newOrderTotal = BigDecimal.ZERO;
        List<PartialReceiptItem> partialItems = new ArrayList<>();
        boolean hasReorderItems = false;

        for (PartialItemRequest itemRequest : request.items()) {
            PurchaseOrderItem originalItem = purchaseOrderItemRepo
                    .findByIdAndPurchaseOrderId(itemRequest.itemId(), originalOrder.getId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "PO item not found for itemId=" + itemRequest.itemId()
                                    + " and purchaseOrderId=" + originalOrder.getId()
                    ));

            validatePartialItem(itemRequest);

            originalItem.setReceivedQty(itemRequest.receivedQty());
            originalItem.setDamagedQty(itemRequest.damagedQty());
            originalItem.setRemainingQty(itemRequest.remainingQty());

            int newOrderQty = safe(itemRequest.newOrderQty());
            PurchaseOrderItem newPoItem = null;

            if (newOrderQty > 0) {
                hasReorderItems = true;

                newPoItem = PurchaseOrderItem.builder()
                        .inventoryItemId(originalItem.getInventoryItemId())
                        .sku(originalItem.getSku())
                        .quantity(newOrderQty)
                        .price(originalItem.getPrice())
                        .receivedQty(0)
                        .damagedQty(0)
                        .remainingQty(newOrderQty)
                        .build();

                newOrder.addItem(newPoItem);

                BigDecimal lineTotal = originalItem.getPrice()
                        .multiply(BigDecimal.valueOf(newOrderQty));
                newOrderTotal = newOrderTotal.add(lineTotal);
            }

            PartialReceiptItem partialItem = PartialReceiptItem.builder()
                    .previousPurchaseOrderItem(originalItem)
                    .newPurchaseOrderItem(newPoItem)
                    .inventoryItemId(originalItem.getInventoryItemId())
                    .sku(originalItem.getSku())
                    .orderedQty(itemRequest.orderedQty())
                    .receivedQty(itemRequest.receivedQty())
                    .damagedQty(itemRequest.damagedQty())
                    .remainingQty(itemRequest.remainingQty())
                    .newOrderQty(newOrderQty)
                    .build();

            partialItems.add(partialItem);
        }

        repo.save(originalOrder);
        shipmentRepo.save(shipment);

        PurchaseOrder savedNewOrder = null;
        if (hasReorderItems) {
            newOrder.setTotalAmount(newOrderTotal);
            savedNewOrder = repo.save(newOrder);
        }

        PartialReceipt partialReceipt = PartialReceipt.builder()
                .previousPurchaseOrder(originalOrder)
                .newPurchaseOrder(savedNewOrder)
                .totalReceived(request.totalReceived())
                .totalDamaged(request.totalDamaged())
                .createdDate(LocalDateTime.now())
                .createdBy("SYSTEM_USER")
                .build();

        partialRepo.save(partialReceipt);

        for (PartialReceiptItem partialItem : partialItems) {
            partialItem.setPartialReceipt(partialReceipt);
        }

        partialItemRepo.saveAll(partialItems);
    }

    private void validateShipmentBelongsToOrder(PurchaseOrder order, Shipment shipment) {
        if (shipment.getPurchaseOrder() == null || !shipment.getPurchaseOrder().getId().equals(order.getId())) {
            throw new IllegalArgumentException("Shipment does not belong to the provided purchase order");
        }
    }

    private void validatePartialItem(PartialItemRequest item) {
        int orderedQty = safe(item.orderedQty());
        int receivedQty = safe(item.receivedQty());
        int damagedQty = safe(item.damagedQty());
        int remainingQty = safe(item.remainingQty());
        int newOrderQty = safe(item.newOrderQty());

        if (orderedQty < 0 || receivedQty < 0 || damagedQty < 0 || remainingQty < 0 || newOrderQty < 0) {
            throw new IllegalArgumentException("Quantities cannot be negative for item: " + item.itemName());
        }

        if (receivedQty + damagedQty > orderedQty) {
            throw new IllegalArgumentException(
                    "Received quantity + damaged quantity cannot exceed ordered quantity for item: " + item.itemName()
            );
        }

        int expectedRemaining = orderedQty - receivedQty - damagedQty;
        if (remainingQty != expectedRemaining) {
            throw new IllegalArgumentException(
                    "Remaining quantity is invalid for item: " + item.itemName()
            );
        }

        int expectedNewOrderQty = remainingQty + damagedQty;
        if (newOrderQty != expectedNewOrderQty) {
            throw new IllegalArgumentException(
                    "New order quantity must equal remaining quantity + damaged quantity for item: " + item.itemName()
            );
        }
    }

    private int safe(Integer value) {
        return value == null ? 0 : value;
    }

    @Transactional
    public void voidPurchaseOrder(String poId, String note, Boolean notifyVendor) {
        if (note == null || note.isBlank()) {
            throw new IllegalArgumentException("Reason is required");
        }

        String cleanNote = note.trim();

        if (cleanNote.length() < 3) {
            throw new IllegalArgumentException("Reason must be a short sentence");
        }

        if (cleanNote.length() > 120) {
            throw new IllegalArgumentException("Reason must be one short sentence only");
        }

        String[] sentences = cleanNote.split("[.!?]+");
        int count = 0;
        for (String s : sentences) {
            if (!s.trim().isEmpty()) {
                count++;
            }
        }

        if (count > 1) {
            throw new IllegalArgumentException("Reason must contain only one sentence");
        }

        PurchaseOrder po = repo.findByPoNumber(poId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found"));

        po.setStatus(PurchaseOrderStatus.VOIDED);
        po.setInternalNotes(cleanNote);

        // optional:
        repo.save(po);
        // po.setNotifyVendor(Boolean.TRUE.equals(notifyVendor));
    }

    @Transactional
    public PurchaseOrder updatePurchaseOrder(String id, CreatePurchaseOrderRequest req) {
        PurchaseOrder po = repo.findByPoNumber(id)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found"));

        po.setVendorId(req.vendorId());
        po.setExpectedDeliveryDate(req.expectedDeliveryDate());
        po.setShippingMethod(req.shippingMethod());
        po.setInternalNotes(req.internalNotes());
        po.setStatus(req.status());
        po.setTotalAmount(req.totalAmount());

        if (po.getItems() == null) {
            po.setItems(new ArrayList<>());
        }

        List<PurchaseOrderItem> existingItems = po.getItems();

        for (var reqItem : req.items()) {
            PurchaseOrderItem matchedItem = existingItems.stream()
                    .filter(existing ->
                            existing.getInventoryItemId() != null
                                    && existing.getInventoryItemId().equals(reqItem.inventoryItemId()))
                    .findFirst()
                    .orElse(null);

            if (matchedItem == null) {
                matchedItem = existingItems.stream()
                        .filter(existing ->
                                existing.getSku() != null
                                        && existing.getSku().equals(reqItem.sku()))
                        .findFirst()
                        .orElse(null);
            }

            if (matchedItem != null) {
                matchedItem.setInventoryItemId(reqItem.inventoryItemId());
                matchedItem.setSku(reqItem.sku());
                matchedItem.setQuantity(reqItem.quantity());
                matchedItem.setPrice(reqItem.price());

                int receivedQty = matchedItem.getReceivedQty() == null ? 0 : matchedItem.getReceivedQty();
                int damagedQty = matchedItem.getDamagedQty() == null ? 0 : matchedItem.getDamagedQty();

                int recalculatedRemainingQty = reqItem.quantity() - receivedQty - damagedQty;
                matchedItem.setRemainingQty(Math.max(recalculatedRemainingQty, 0));
            } else {
                PurchaseOrderItem newItem = new PurchaseOrderItem();
                newItem.setInventoryItemId(reqItem.inventoryItemId());
                newItem.setSku(reqItem.sku());
                newItem.setQuantity(reqItem.quantity());
                newItem.setPrice(reqItem.price());
                newItem.setReceivedQty(0);
                newItem.setDamagedQty(0);
                newItem.setRemainingQty(reqItem.quantity());
                newItem.setPurchaseOrder(po);

                existingItems.add(newItem);
            }
        }

        for (PurchaseOrderItem existingItem : existingItems) {
            boolean existsInRequest = req.items().stream().anyMatch(reqItem ->
                    (existingItem.getInventoryItemId() != null
                            && existingItem.getInventoryItemId().equals(reqItem.inventoryItemId()))
                            ||
                            (existingItem.getSku() != null
                                    && existingItem.getSku().equals(reqItem.sku()))
            );

            if (!existsInRequest) {
                throw new IllegalStateException(
                        "Cannot remove existing item with SKU: " + existingItem.getSku() +
                                ". This item may already be referenced by partial receipts."
                );
            }
        }

        return repo.save(po);
    }

    public ResponseEntity<PurchaseOrderResponse> getOrderByID(String id) {
        return repo.findByPoNumber(id)
                .map(this::mapToResponse) // Maps the single PO to a Response
                .map(ResponseEntity::ok)   // Wraps it in 200 OK
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found"));
    }

    private PurchaseOrderResponse mapToResponse(PurchaseOrder po) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        VendorResponse vendor = vendorClient.getVendor(po.getVendorId());


        return new PurchaseOrderResponse(
                po.getPoNumber(),
                vendor != null ? vendor.getCompanyName() : "Unknown Vendor",// Consider moving this to a constant or DB field
                po.getStatus().name(),
                po.getCreatedDate().format(formatter),
                po.getExpectedDeliveryDate() != null
                        ? po.getExpectedDeliveryDate().format(formatter)
                        : null,
                po.getTotalAmount(),
                po.getCreatedBy(),
                po.getItems().stream()
                        .map(PurchaseOrderItem::getSku)
                        .toList()
        );
    }
}