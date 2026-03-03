package com.example.financialledger.service;

import com.example.financialledger.dto.PaymentDetailsDTO;
import com.example.financialledger.model.FinancialTransaction;
import com.example.financialledger.model.PaymentHistory;
import com.example.financialledger.repository.PaymentRepository;
import com.example.financialledger.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class PaymentService {

        @Autowired
        private PaymentRepository paymentRepository;

        @Autowired
        private TransactionRepository transactionRepository;

        // 🔹 Used by main table (ALL debts)
        public List<PaymentDetailsDTO> getAllPaymentDetails() {

                // 1. Get all EXPENSE transactions (Accounts Payable)
                List<FinancialTransaction> transactions = transactionRepository.findAll();

                return transactions.stream()
                                .filter(tx -> tx.getType() == FinancialTransaction.TransactionType.Purchase)
                                .map(tx -> {

                                        // 2. Get payments for this transaction
                                        List<PaymentHistory> payments = paymentRepository
                                                        .findByTransactionId(tx.getId());

                                        // 3. Total paid
                                        BigDecimal totalPaid = payments.stream()
                                                        .map(PaymentHistory::getAmountPaid)
                                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                                        // 4. Remaining balance
                                        BigDecimal remaining = tx.getTotal().subtract(totalPaid);

                                        // 5. Latest due date (if multiple payments)
                                        LocalDateTime dueDate = payments.stream()
                                                        .map(PaymentHistory::getDueDate)
                                                        .filter(Objects::nonNull)
                                                        .max(LocalDateTime::compareTo)
                                                        .orElse(null);

                                        // 6. Return ONE row per transaction
                                        return new PaymentDetailsDTO(
                                                        null, // paymentId not needed in table
                                                        totalPaid, // amountPaid (SUM)
                                                        null, // paymentDate not needed
                                                        null,
                                                        null,
                                                        dueDate,
                                                        remaining,

                                                        tx.getId(),
                                                        tx.getOrderId(),
                                                        tx.getPartyName(),
                                                        tx.getTotal(),
                                                        tx.getStatus().name());
                                })
                                .toList();
        }

        public List<PaymentDetailsDTO> getPaymentDetails(String id) {
                FinancialTransaction transaction = transactionRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));

                List<PaymentHistory> payments = paymentRepository.findByTransactionId(id);

                BigDecimal totalPaid = payments.stream()
                                .map(PaymentHistory::getAmountPaid)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal remainingBalance = transaction.getTotal().subtract(totalPaid);

                return payments.stream()
                                .map(p -> new PaymentDetailsDTO(
                                                p.getPaymentId(),
                                                p.getAmountPaid(),
                                                p.getPaymentDate(),
                                                p.getPaymentMethod(),
                                                p.getReferenceNumber(),
                                                p.getDueDate(),
                                                remainingBalance,

                                                transaction.getId(),
                                                transaction.getOrderId(),
                                                transaction.getPartyName(),
                                                transaction.getTotal(),
                                                transaction.getStatus().name()))
                                .toList();
        }

        public void savePayment(PaymentHistory payment) {
                // 1. Save payment record
                paymentRepository.save(payment);

                // 2. Update transaction status if fully paid
                FinancialTransaction transaction = transactionRepository.findById(payment.getTransactionId())
                                .orElseThrow(() -> new RuntimeException("Transaction not found"));

                List<PaymentHistory> allPayments = paymentRepository.findByTransactionId(payment.getTransactionId());
                BigDecimal totalPaid = allPayments.stream()
                                .map(PaymentHistory::getAmountPaid)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                if (totalPaid.compareTo(transaction.getTotal()) >= 0) {
                        transaction.setStatus(FinancialTransaction.Status.Paid);
                }
                transactionRepository.save(transaction);
        }
}
