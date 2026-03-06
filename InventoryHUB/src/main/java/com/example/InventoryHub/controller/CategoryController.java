package com.example.InventoryHub.controller;

import com.example.InventoryHub.model.Category;
import com.example.InventoryHub.repo.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@CrossOrigin(origins = "http://localhost:3000") // Matches your React URL
public class CategoryController {

    private final ICategoryRepository categoryRepository;

    public CategoryController(ICategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public Category createCategory(@RequestBody Category category) {
        // This saves the new category name into the 'categories' table
        return categoryRepository.save(category);
    }

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}