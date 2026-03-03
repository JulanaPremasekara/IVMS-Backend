package com.example.InventoryHub.repo;

import com.example.InventoryHub.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<Product, Long> {

    // This name MUST match what you call in the Service
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.skuCode) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Product> searchProducts(@Param("query") String query);
    // For the main Inventory Hub list (Active only)
    List<Product> findAllByIsActiveTrue();

    // For the Archive List (Hidden items only)
    List<Product> findAllByIsActiveFalse();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.skuCode LIKE %:query% OR p.name LIKE %:query%)")
    List<Product> searchActiveForArchive(@Param("query") String query, boolean active);

    // Search both depending on toggle
    List<Product> findByIsActiveAndNameContainingIgnoreCase(boolean isActive, String name);


}