package com.example.demo;

import com.example.demo.dto.StockAdjustmentDTO;
import com.example.demo.model.Product;
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


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductStockModificationTest {

    @Mock
    private IProductRepository productRepository;
    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void updateStock_EdgeCase_ProductNotFound() {
        StockAdjustmentDTO dto = new StockAdjustmentDTO(1L, 100, "Manual");

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            productService.updateStockThresholds(dto);
        });
    }

    @Test
    void updateStock_EdgeCase_NegativeValues() {
        StockAdjustmentDTO dto = new StockAdjustmentDTO(1L, -5, "Manual");

        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStockThresholds(dto);
        });
    }
}