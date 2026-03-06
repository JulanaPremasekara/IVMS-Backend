package com.example.financialledger.service;

import com.example.financialledger.model.FinancialTransaction;
import com.example.financialledger.repository.PaymentRepository;
import com.example.financialledger.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class FinancialService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private PaymentRepository paymentRepository;

    public List<FinancialTransaction> getAllTransactions() {
        return repository.findAll();
    }

    public BigDecimal getTotalRevenue() {
        return repository.getTotalRevenue();
    }

    public BigDecimal getTotalExpenses() {
        return repository.getTotalExpenses();
    }

    public BigDecimal getOutstandingAmount() {
        return repository.getOutstandingAmount();
    }

    @Transactional
    public void deleteTransaction(String id) {
        paymentRepository.deleteByTransactionId(id);
        repository.deleteById(id);
    }
}