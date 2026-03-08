package com.example.demo.repo;



import com.example.demo.entity.PurchaseOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, Long> {

    Optional<PurchaseOrderItem> findByIdAndPurchaseOrderId(Long itemId, Long purchaseOrderId);
}
