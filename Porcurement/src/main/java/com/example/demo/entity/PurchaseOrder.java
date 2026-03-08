package com.example.demo.entity;

import com.example.demo.enums.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@ToString(exclude = {"items", "shipment", "outgoingPartialReceipts", "incomingPartialReceipts"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(name = "po_number", nullable = false, unique = true, updatable = false)
    private String poNumber;

    @Column(nullable = false)
    private Long vendorId;

    private LocalDate expectedDeliveryDate;

    @Column(nullable = false)
    private String shippingMethod;

    @Column(length = 2000)
    private String internalNotes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PurchaseOrderStatus status;

    @Column(nullable = false)
    private BigDecimal totalAmount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Builder.Default
    @OneToMany(
            mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PurchaseOrderItem> items = new ArrayList<>();

    @OneToOne(
            mappedBy = "purchaseOrder",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private Shipment shipment;

    /*
     PARTIAL RECEIPT NAVIGATION
     */

    @Builder.Default
    @OneToMany(mappedBy = "previousPurchaseOrder")
    private List<PartialReceipt> outgoingPartialReceipts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "newPurchaseOrder")
    private List<PartialReceipt> incomingPartialReceipts = new ArrayList<>();

    /*
     HELPER METHODS
     */

    public void addItem(PurchaseOrderItem item) {
        items.add(item);
        item.setPurchaseOrder(this);
    }

    public void setItems(List<PurchaseOrderItem> newItems) {
        items.clear();
        if (newItems != null) {
            newItems.forEach(this::addItem);
        }
    }

    public void attachShipment(Shipment shipment) {
        this.shipment = shipment;
        shipment.setPurchaseOrder(this);
    }

    public void setShipment(Shipment shipment) {
        this.shipment = shipment;
        if (shipment != null) {
            shipment.setPurchaseOrder(this);
        }
    }
}