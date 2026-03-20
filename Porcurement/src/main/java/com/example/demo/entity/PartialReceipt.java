package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "partial_receipts")

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

@ToString(exclude = {"previousPurchaseOrder", "newPurchaseOrder", "items"})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PartialReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "previous_purchase_order_id", nullable = false)
    private PurchaseOrder previousPurchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_purchase_order_id")
    private PurchaseOrder newPurchaseOrder;

    @Column(nullable = false)
    private Integer totalReceived;

    @Column(nullable = false)
    private Integer totalDamaged;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Builder.Default
    @OneToMany(
            mappedBy = "partialReceipt",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PartialReceiptItem> items = new ArrayList<>();

    public void addItem(PartialReceiptItem item) {
        items.add(item);
        item.setPartialReceipt(this);
    }

    public void setItems(List<PartialReceiptItem> newItems) {
        items.clear();
        if (newItems != null) {
            newItems.forEach(this::addItem);
        }
    }
}