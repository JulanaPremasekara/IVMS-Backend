package com.example.financialledger.repository;

import com.example.financialledger.model.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentHistory, Long> {
    // id = FinancialTransaction id (foreign key)
    List<PaymentHistory> findByTransactionId(String transactionId);

    void deleteByTransactionId(String transactionId);
}
