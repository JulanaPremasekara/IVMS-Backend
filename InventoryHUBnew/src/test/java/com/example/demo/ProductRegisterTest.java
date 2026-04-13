package com.example.demo;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.dto.ProductRegistrationDTO;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.model.StockProducer;
import com.example.demo.repo.ICategoryRepository;
import com.example.demo.repo.IProductRepository;
import com.example.demo.service.IProductService;
import com.example.demo.service.ProductServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductRegisterTest {

    @Mock private IProductRepository productRepository;
    @Mock private ICategoryRepository categoryRepository;
    @InjectMocks
    private ProductServiceImpl productService;
    @Mock
    private StockProducer stockProducer;

    @Test
    @DisplayName("Test 1: Successful Registration (Happy Path)")
    void registerProduct_Success() {
        // Arrange
        ProductRegistrationDTO dto = createValidDTO();
        Category mockCategory = new Category(1L, "Electronics");

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(mockCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Product result = productService.registerProduct(dto);

        // Assert
        assertNotNull(result.getSkuCode());
        assertTrue(result.getSkuCode().startsWith("SKU-"));
        assertEquals("Samsung Fridge", result.getName());
        assertTrue(result.isActive());
        verify(productRepository, times(1)).save(any(Product.class));


    }

    @Test
    @DisplayName("Test 2: Optional Tech Specs are Null (Edge Case)")
    void registerProduct_NullOptionalFields() {
        // Arrange: DTO with only mandatory fields, others are null
        ProductRegistrationDTO dto = createValidDTO();
        dto.setLength(null);
        dto.setPowerUsage(null);

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Product result = productService.registerProduct(dto);

        // Assert
        assertNull(result.getLength());
        assertNull(result.getPowerUsage());
        assertNotNull(result.getSkuCode());
    }

    @Test
    @DisplayName("Test 3: Stock at Zero Boundary (Edge Case)")
    void registerProduct_ZeroStock() {
        // Arrange
        ProductRegistrationDTO dto = createValidDTO();
        dto.setInitialStock(0); // Minimum boundary for @Min(0)

        when(categoryRepository.findByName(anyString())).thenReturn(Optional.of(new Category()));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Product result = productService.registerProduct(dto);

        // Assert
        assertEquals(0, result.getInitialStock());
    }

    @Test
    @DisplayName("Test 4: Category Not Found -> Create New Category")
    void registerProduct_CategoryNotFound() {
        ProductRegistrationDTO dto = createValidDTO();

        Category newCategory = new Category();
        newCategory.setId(1L);
        newCategory.setName("Electronics");

        when(categoryRepository.findByName("Electronics")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        Product result = productService.registerProduct(dto);

        assertNotNull(result);
        assertEquals("Electronics", result.getCategory().getName());

        verify(categoryRepository, times(1)).save(any(Category.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // Helper method for clean code
    private ProductRegistrationDTO createValidDTO() {
        return ProductRegistrationDTO.builder()
                .productName("Samsung Fridge")
                .category("Electronics")
                .unitPrice(1200.0)
                .initialStock(10)
                .warehouseLocation("A-12-03")
                .build();
    }
}
