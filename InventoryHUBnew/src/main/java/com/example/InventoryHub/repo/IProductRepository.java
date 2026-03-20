package com.example.InventoryHub.repo;

import com.example.InventoryHub.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

    // This handles partial words (LIKE %query%) and case sensitivity (IgnoreCase)
    // It searches BOTH Name and SKU.
    List<Product> findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(String name, String sku);

    // Keep your status-based lists for the main dashboard
    List<Product> findAllByIsActiveTrue();
    List<Product> findAllByIsActiveFalse();
}