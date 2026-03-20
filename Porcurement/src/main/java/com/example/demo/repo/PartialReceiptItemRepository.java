package com.example.demo.repo;

import com.example.demo.entity.PartialReceiptItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartialReceiptItemRepository extends JpaRepository<PartialReceiptItem, Long> {
}