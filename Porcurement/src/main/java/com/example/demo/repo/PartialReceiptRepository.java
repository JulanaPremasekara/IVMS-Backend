package com.example.demo.repo;

import com.example.demo.entity.PartialReceipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartialReceiptRepository extends JpaRepository<PartialReceipt, Long> {
}