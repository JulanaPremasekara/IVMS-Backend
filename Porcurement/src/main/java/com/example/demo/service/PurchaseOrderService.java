package com.example.demo.service;

import com.example.demo.dto.CreatePurchaseOrderRequest;
import com.example.demo.dto.PurchaseOrderResponse;
import com.example.demo.entity.PurchaseOrder;
import com.example.demo.entity.PurchaseOrderItem;
import com.example.demo.entity.Shipment;
import com.example.demo.enums.PurchaseOrderStatus;
import com.example.demo.enums.ShipmentStatus;
import com.example.demo.repo.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class PurchaseOrderService {

    private final PurchaseOrderRepository repo;

    public PurchaseOrderService(PurchaseOrderRepository repo) {
        this.repo = repo;
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
            return item;
        }).toList();

        po.setItems(poItems); // uses helper to connect parent FK

        return repo.save(po); // ✅ cascades and saves weak items
    }

    private String generatePoNumber() {

        LocalDate today = LocalDate.now();

        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        long todayCount = repo.countByCreatedDateBetween(startOfDay, endOfDay);

        long nextNumber = todayCount + 1;

        return String.format(
                "PO-%s-%04d",
                today,
                nextNumber
        );
    }


    public List<PurchaseOrderResponse> mapToResponseList(
            List<PurchaseOrder> purchaseOrders
    ) {

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMM dd, yyyy");

        return purchaseOrders.stream()
                .map(po -> new PurchaseOrderResponse(
                        po.getPoNumber(),                      // id
                        "TechSupply Inc.",                     // temporary vendor name
                        po.getStatus().name(),                 // status
                        po.getCreatedDate().format(formatter), // date
                        po.getExpectedDeliveryDate() != null
                                ? po.getExpectedDeliveryDate().format(formatter)
                                : null,
                        po.getTotalAmount(),                   // total
                        po.getCreatedBy(),                     // creator
                        po.getItems()
                                .stream()
                                .map(PurchaseOrderItem::getSku)
                                .toList()
                ))
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
        // optionally: po.setApprovedBy(supplierUser);

        // ✅ Weak entity creation (Shipment depends on PO)
        if (po.getShipment() == null) {
            Shipment shipment = Shipment.builder()
                    .vendorId(po.getVendorId())
                    .origin("TBD") // replace with your actual origin logic
                    .destination("TBD") // replace with your actual destination logic
                    .status(ShipmentStatus.PREPARING)
                    .trackingId(null)
                    .estimatedArrival(po.getExpectedDeliveryDate())
                    .createdDate(LocalDateTime.now())
                    .build();

            po.attachShipment(shipment); // sets both sides (PO.shipment and shipment.purchaseOrder)
        }

        return repo.save(po); // cascade persists shipment
    }

    public PurchaseOrder get(Long poId) {
        return repo.findById(poId)
                .orElseThrow(() -> new IllegalArgumentException("PO not found: " + poId));
    }

}
