package com.example.financialledger.repository;

import com.example.financialledger.model.FinancialTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface TransactionRepository extends JpaRepository<FinancialTransaction, String> {

    // Spring Data JPA provides findAll() automatically!
    @Query("SELECT COALESCE(SUM(t.total), 0) FROM FinancialTransaction t WHERE t.type = com.example.financialledger.model.FinancialTransaction.TransactionType.Sale")
    BigDecimal getTotalRevenue();

    @Query("SELECT COALESCE(SUM(t.total), 0) FROM FinancialTransaction t WHERE t.type = com.example.financialledger.model.FinancialTransaction.TransactionType.Purchase")
    BigDecimal getTotalExpenses();

    @Query("SELECT COALESCE(SUM(t.total), 0) FROM FinancialTransaction t WHERE t.status = com.example.financialledger.model.FinancialTransaction.Status.Pending")
    BigDecimal getOutstandingAmount();
}
