package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "partial_receipt_items")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@ToString(exclude = {"partialReceipt", "previousPurchaseOrderItem", "newPurchaseOrderItem"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartialReceiptItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "partial_receipt_id", nullable = false)
    private PartialReceipt partialReceipt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "previous_purchase_order_item_id", nullable = false)
    private PurchaseOrderItem previousPurchaseOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_purchase_order_item_id")
    private PurchaseOrderItem newPurchaseOrderItem;

    @Column(nullable = false)
    private Long inventoryItemId;

    private String sku;

    @Column(nullable = false)
    private Integer orderedQty;

    @Column(nullable = false)
    private Integer receivedQty;

    @Column(nullable = false)
    private Integer damagedQty;

    @Column(nullable = false)
    private Integer remainingQty;

    @Column(nullable = false)
    private Integer newOrderQty;
}