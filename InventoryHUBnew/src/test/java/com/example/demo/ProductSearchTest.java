package com.example.demo;

import com.example.demo.model.Product;
import com.example.demo.repo.IProductRepository;
import com.example.demo.service.IProductService;
import com.example.demo.service.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductSearchTest {

    @Mock
    private IProductRepository productRepository; // Use your actual Repository interface

    @InjectMocks
    private ProductServiceImpl productService; // Use your actual Service class

    @Test
    void searchInventory_ShouldReturnResults_WhenMatchExists() {
        // GIVEN: Data exists in the "mock" database
        String query = "Samsung";
        Product p1 = Product.builder().name("Samsung Fridge").skuCode("SKU-01").build();

        // Define behavior for your actual repository method
        when(productRepository.findByNameContainingIgnoreCaseOrSkuCodeContainingIgnoreCase(query, query))
                .thenReturn(List.of(p1));

        // WHEN: Calling your actual service method
        List<Product> results = productService.searchProducts(query);

        // THEN: Verify the logic
        assertEquals(1, results.size());
        assertEquals("Samsung Fridge", results.get(0).getName());
    }

    @Test
    void searchInventory_EdgeCase_EmptyQuery() {
        List<Product> results = productService.searchProducts("");

        assertTrue(results.isEmpty());
        verifyNoInteractions(productRepository);
    }
}