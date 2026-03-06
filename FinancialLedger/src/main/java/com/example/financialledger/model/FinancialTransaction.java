package com.example.financialledger.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

@Entity
@Table(name = "Financial_Ledger")
public class FinancialTransaction {

    @Id
    @Column(length = 20)
    private String id; // AUTO-GENERATED: INV-2026-001

    @Column(name = "order_id")
    private String orderId;

    @Enumerated(EnumType.STRING)
    private TransactionType type; // Sale / Purchase

    @Column(name = "party_name")
    private String partyName;

    private BigDecimal amount;

    @Column(name = "tax_rate")
    private BigDecimal tax;

    @Column(name = "total_amount")
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FiscalPeriod fiscalPeriod;

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Column(nullable = true)
    private String category;

    // ---------- AUTO ID GENERATION ----------
    @PrePersist
    public void generateId() {
        if (this.id == null) {
            String prefix = (this.type == TransactionType.Sale) ? "INV" : "EXP";
            int year = Year.now().getValue();
            String sequence = String.format("%03d", System.currentTimeMillis() % 1000);
            this.id = prefix + "-" + year + "-" + sequence;
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    // ---------- ENUMS ----------
    public enum TransactionType {
        Sale, Purchase
    }

    public enum Status {
        Paid, Pending, Voided
    }

    // ---------- GETTERS & SETTERS ----------
    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        this.tax = tax;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FiscalPeriod getFiscalPeriod() {
        return fiscalPeriod;
    }

    public void setFiscalPeriod(FiscalPeriod fiscalPeriod) {
        this.fiscalPeriod = fiscalPeriod;
    }
}