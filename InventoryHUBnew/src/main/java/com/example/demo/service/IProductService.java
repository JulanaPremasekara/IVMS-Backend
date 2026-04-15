package com.example.demo.service;

import com.example.demo.dto.ProductRegistrationDTO;
import com.example.demo.dto.ProductSummaryDTO;
import com.example.demo.dto.StockAdjustmentDTO;
import com.example.demo.model.Product;

import java.util.List;

public interface IProductService {
    Product registerProduct(ProductRegistrationDTO dto);
    List<Product> getAllProducts();
    void archiveProduct(Long productId);
    Product updateStock(Long productId, Integer newQuantity);
    List<Product> searchProducts(String query);
    void restoreProduct(Long productId);

    // Pro-Tip: You'll also need this for your new Archive Management screen
    List<Product> getArchivedProducts();


    Product updateStockThresholds(StockAdjustmentDTO dto);

    Product getProductById(Long id);
}

