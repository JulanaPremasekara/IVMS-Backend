package com.example.demo;

import com.example.demo.model.Product;
import com.example.demo.repo.IProductRepository;
import com.example.demo.service.IProductService;
import com.example.demo.service.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductArchiveTest {

    @Mock
    private IProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    @DisplayName("Happy Path: Archive succeeds when stock is exactly 0")
    void archive_Success_WhenStockIsZero() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .skuCode("SKU002")
                .initialStock(0)
                .isActive(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.archiveProduct(productId);

        assertFalse(product.isActive(), "Product should be set to inactive");
        assertNotNull(product.getArchivedAt(), "Archived date should be recorded");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("Edge Case: Archive fails when stock is greater than 0")
    void archive_Fail_WhenStockIsNotEmpty() {
        // Arrange
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .skuCode("SKU002")
                .initialStock(50)
                .isActive(true)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            productService.archiveProduct(productId);
        });

        assertEquals(
                "Stock Remains: 50 units exist. Please 'Write-off' or 'Clearance Sale' first. ",
                exception.getMessage()
        );
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Edge Case: Archive fails if product is already archived")
    void archive_Fail_IfAlreadyArchived() {
        Long productId = 1L;

        Product product = Product.builder()
                .id(productId)
                .skuCode("SKU002")
                .initialStock(0)
                .isActive(false)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        productService.archiveProduct(productId);

        assertFalse(product.isActive());
        verify(productRepository, times(1)).save(product);
    }
}