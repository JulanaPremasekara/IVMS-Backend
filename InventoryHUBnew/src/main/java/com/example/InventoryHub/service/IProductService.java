package com.example.InventoryHub.service;

import com.example.InventoryHub.dto.ProductRegistrationDTO;
import com.example.InventoryHub.model.Product;

import java.util.List;

public interface IProductService {
    Product registerProduct(ProductRegistrationDTO dto);
    List<Product> getAllProducts();
    void archiveProduct(Long productId);
    void updateStock(Long productId, Integer newQuantity);
    List<Product> searchProducts(String query);
    void restoreProduct(Long productId);

    // Pro-Tip: You'll also need this for your new Archive Management screen
    List<Product> getArchivedProducts();


}

