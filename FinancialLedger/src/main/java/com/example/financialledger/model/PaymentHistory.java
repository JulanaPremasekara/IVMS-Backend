package com.example.financialledger.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

    @Entity
    @Table(name = "PAYMENT_HISTORY")
    public class PaymentHistory {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long paymentId;

        private BigDecimal amountPaid;
        private LocalDateTime paymentDate;
        private String paymentMethod;
        private String referenceNumber;
        private LocalDateTime dueDate;
        private BigDecimal remainingBalance;

        public BigDecimal getRemainingBalance() {
            return remainingBalance;
        }

        public void setRemainingBalance(BigDecimal remainingBalance) {
            this.remainingBalance = remainingBalance;
        }

        public LocalDateTime getDueDate() {
            return dueDate;
        }

        public void setDueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
        }

        // Links to your FinancialTransaction (Expense ID)
        private String transactionId;

        public Long getPaymentId() {
            return paymentId;
        }

        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }

        public BigDecimal getAmountPaid() {
            return amountPaid;
        }

        public void setAmountPaid(BigDecimal amountPaid) {
            this.amountPaid = amountPaid;
        }

        public LocalDateTime getPaymentDate() {
            return paymentDate;
        }

        public void setPaymentDate(LocalDateTime paymentDate) {
            this.paymentDate = paymentDate;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }

        public String getTransactionId() {
            return transactionId;
        }

        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
        }

        // Constructors, Getters, Setters

    }
