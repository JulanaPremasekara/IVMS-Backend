package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_items")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@ToString(exclude = "purchaseOrder")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PurchaseOrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;

    @Column(nullable = false)
    private Long inventoryItemId;

    private String sku;

    /*
     quantity = originally ordered quantity
    */
    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    /*
     NEW FIELDS FOR RECEIVING
    */

    @Column(nullable = false)
    @Builder.Default
    private Integer receivedQty = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer damagedQty = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer remainingQty = 0;
}