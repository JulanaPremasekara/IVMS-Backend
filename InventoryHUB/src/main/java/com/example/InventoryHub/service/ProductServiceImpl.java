package com.example.InventoryHub.service;

import com.example.InventoryHub.dto.ProductRegistrationDTO;
import com.example.InventoryHub.dto.ProductSummaryDTO;
import com.example.InventoryHub.exception.ResourceNotFoundException;
import com.example.InventoryHub.model.Category;
import com.example.InventoryHub.model.Product;
import com.example.InventoryHub.model.StockProducer;
import com.example.InventoryHub.repo.ICategoryRepository;
import com.example.InventoryHub.repo.IProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    private final StockProducer stockProducer; // 1. Add this field

    // 2. Add StockProducer to the constructor
    public ProductServiceImpl(IProductRepository productRepository,
                              ICategoryRepository categoryRepository,
                              StockProducer stockProducer) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockProducer = stockProducer;
    }


    @Override
    @Transactional
    public Product registerProduct(ProductRegistrationDTO dto) {
        Category category = categoryRepository.findByName(dto.getCategory())
                .orElseGet(() -> {
                    Category newCat = new Category();
                    newCat.setName(dto.getCategory());
                    return categoryRepository.save(newCat);
                });

        String sku = "INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Product product = Product.builder()
                .skuCode(sku)
                .name(dto.getProductName())
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



        // 1. Save to Database first
        Product savedProduct = productRepository.save(product);

        // 2. Map the saved data to your Summary DTO
        ProductSummaryDTO summary = ProductSummaryDTO.builder()
                .id(savedProduct.getId())
                .skuCode(savedProduct.getSkuCode())
                .name(savedProduct.getName())
                .currentStock(savedProduct.getInitialStock()) // This is your current stock
                .reorderThreshold(savedProduct.getReorderThreshold()) // Your safety limit
                .isActive(true)
                .build();

        // 3. Trigger the RabbitMQ Message
        stockProducer.sendStockUpdate(summary);

        return savedProduct;
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public void archiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Check Requirement: Stock Remains
        if (product.getInitialStock() > 0) {
            throw new IllegalStateException("Stock Remains: System prevents deletion. Please 'Write-off' or 'Clearance Sale' first.");
        }

        // REMOVED: The check for "Pending Orders" since we don't have an Order entity/repo yet.

        // Soft Delete
        product.setActive(false);
        productRepository.save(product);

        System.out.println("Product " + id + " has been archived.");
    }
    @Override
    @Transactional
    public void updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setInitialStock(quantity);
        Product updatedProduct = productRepository.save(product);

        // TRIGGER THE QUEUE: This is what solves the "database exhaustion" concern
        ProductSummaryDTO summary = ProductSummaryDTO.builder()
                .id(updatedProduct.getId())
                .name(updatedProduct.getName())
                .currentStock(updatedProduct.getInitialStock())
                .reorderThreshold(updatedProduct.getReorderThreshold())
                .skuCode(updatedProduct.getSkuCode())
                .build();

        stockProducer.sendStockUpdate(summary); // Sends to RabbitMQ
    }
    @Override
    public List<Product> searchProducts(String query) {
        // This assumes your repository has a search method like findByNameContaining
        return productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query);
    }


}