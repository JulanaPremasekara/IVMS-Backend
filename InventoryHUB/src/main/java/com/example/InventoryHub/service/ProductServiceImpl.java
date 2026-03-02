package com.example.InventoryHub.service;

import com.example.InventoryHub.dto.ProductRegistrationDTO;
import com.example.InventoryHub.model.Category;
import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.repo.ICategoryRepository;
import com.example.InventoryHub.repo.IProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository; // 1. Add this

    // 2. Add categoryRepository to the constructor
    public ProductServiceImpl(IProductRepository productRepository, ICategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public Product registerProduct(ProductRegistrationDTO dto) {
        // 1. IMPROVED: Find or Create the Category automatically
        Category category = categoryRepository.findByName(dto.getCategory())
                .orElseGet(() -> {
                    System.out.println("Category not found, creating new one: " + dto.getCategory());
                    Category newCat = new Category();
                    newCat.setName(dto.getCategory());
                    return categoryRepository.save(newCat);
                });


        // Business Rule: Auto-generate SKU
        String sku = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 2. Map to Entity (Using your existing Builder)
        Product product = Product.builder()
                .skuCode(sku)
                .name(dto.getProductName()) // Ensure this matches your Product model field name
                .category(category)
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .powerUsage(dto.getPowerUsage())
                .weight(dto.getWeight())
                .description(dto.getDescription())
                .unitPrice(dto.getUnitPrice())
                .initialStock(dto.getInitialStock())
                .warehouseLocation(dto.getWarehouseLocation())
                .reorderThreshold(dto.getReorderThreshold())
                .build();

        // 3. Save and return
        return productRepository.save(product);
    }
    @Override
    public List<Product> getAllProducts() {
        // This pulls all saved products from MySQL
        return productRepository.findAll();
    }
}