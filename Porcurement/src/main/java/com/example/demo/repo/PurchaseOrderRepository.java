package com.example.demo.repo;

import com.example.demo.entity.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    long countByCreatedDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("""
           SELECT DISTINCT p
           FROM PurchaseOrder p
           LEFT JOIN FETCH p.items
           ORDER BY p.poNumber DESC
           """)
    List<PurchaseOrder> findAllWithItemsOrderByPoNumberDesc();

    Optional<PurchaseOrder> findByPoNumber(String poNumber);
    boolean existsByPoNumber(String poNumber);
}
