package com.example.demo.service;

import com.example.demo.dto.ProductRegistrationDTO;
import com.example.demo.dto.ProductSummaryDTO;
import com.example.demo.dto.StockAdjustmentDTO;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.StockProducer;
import com.example.demo.repo.ICategoryRepository;
import com.example.demo.repo.IProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductServiceImpl implements IProductService {

    private final IProductRepository productRepository;
    private final ICategoryRepository categoryRepository;
    private final StockProducer stockProducer;

    public ProductServiceImpl(IProductRepository productRepository,
                              ICategoryRepository categoryRepository,
                              StockProducer stockProducer) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.stockProducer = stockProducer;
    }

    @Override
    public List<Product> getAllProducts() {
        // Fulfills UC-104: Product is hidden from active lists
        return productRepository.findAllByIsActiveTrue();
    }

    @Override
    @Transactional
    public void archiveProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // UC-104 Precondition: Stock must be zero
        if (product.getInitialStock() > 0) {
            throw new IllegalStateException("Stock Remains: " + product.getInitialStock() +
                    " units exist. Please 'Write-off' or 'Clearance Sale' first. ");
        }

        // Soft Delete: Lombok converts 'boolean isActive' to 'setActive'
        product.setActive(false);
        product.setArchivedAt(LocalDateTime.now());
        productRepository.save(product);

        System.out.println("Product " + productId + " successfully archived.");
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

        String sku = "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Product product = Product.builder()
                .skuCode(sku)
                .name(dto.getProductName())
                .category(category)
                .unitPrice(dto.getUnitPrice())
                .initialStock(dto.getInitialStock())
                .warehouseLocation(dto.getWarehouseLocation())
                .reorderThreshold(dto.getReorderThreshold())
                .isActive(true) // Ensure new products are active by default [cite: 15]
                .build();

        Product savedProduct = productRepository.save(product);

        // Notify AI Engine of new SKU via RabbitMQ [cite: 26, 38]
        stockProducer.sendStockUpdate(ProductSummaryDTO.builder()
                .id(savedProduct.getId())
                .skuCode(savedProduct.getSkuCode())
                .name(savedProduct.getName())
                .currentStock(savedProduct.getInitialStock())
                .reorderThreshold(savedProduct.getReorderThreshold())
                .isActive(true)
                .build());

        return savedProduct;
    }

    @Override
    @Transactional
    public Product updateStock(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setInitialStock(quantity);
        productRepository.save(product);

        // Push update to AI Decision Engine [cite: 26]
        stockProducer.sendStockUpdate(ProductSummaryDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .currentStock(product.getInitialStock())
                .reorderThreshold(product.getReorderThreshold())
                .skuCode(product.getSkuCode())
                .build());
        return product;
    }

    @Override
    public List<Product> searchProducts(String query) {
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }

        return productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query);
    }

    @Override
    @Transactional
    public void restoreProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.setActive(true); // Flips the soft-delete back to active
        productRepository.save(product);
    }

    @Override
    public List<Product> getArchivedProducts() {
        // This calls the repository method we added earlier
        return productRepository.findAllByIsActiveFalse();
    }

    @Override
    public Product updateStockThresholds(StockAdjustmentDTO dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        if (dto.getNewSafetyLimit() < 0) {
            throw new IllegalArgumentException("Safety limit cannot be negative");
        }

        product.setReorderThreshold(dto.getNewSafetyLimit());

        return productRepository.save(product);
    }
    @Override
    public Product getProductById (Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return product;
    }
}