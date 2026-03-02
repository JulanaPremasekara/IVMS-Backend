package com.example.InventoryHub.repo;

import com.example.InventoryHub.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    // This removes the "Cannot resolve method findAll/save" errors
}