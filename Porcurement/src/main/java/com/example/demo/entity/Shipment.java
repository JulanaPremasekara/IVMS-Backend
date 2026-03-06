package com.example.demo.entity;

import com.example.demo.enums.ShipmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@ToString(exclude = "purchaseOrder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Shipment {

    // ✅ Shared PK = PO ID
    @Id
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId // ✅ uses purchaseOrder.id as this entity id
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(nullable = false)
    private Long vendorId;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status;

    private String trackingId;
    private LocalDate estimatedArrival;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;
}
