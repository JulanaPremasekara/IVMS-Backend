package com.example.InventoryHub.service;

import com.example.InventoryHub.dto.ProductRegistrationDTO;
import com.example.InventoryHub.model.Product;

import java.util.List;

public interface IProductService {
    Product registerProduct(ProductRegistrationDTO dto);
    List<Product> getAllProducts();
}
