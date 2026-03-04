package com.example.InventoryHub.repo;

import jakarta.persistence.criteria.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// You will need an Order entity to map this to your orders table
@Repository
public interface IOrderRepository extends JpaRepository<Order, Long> {

    // This is the exact method your Service logic is looking for
    boolean existsBySkuCodeAndStatus(String skuCode, String status);
}
