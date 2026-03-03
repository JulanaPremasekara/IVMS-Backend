package com.example.financialledger.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "FISCAL_PERIODS")
public class FiscalPeriod {

    @Id
    @Column(name = "transaction_id")
    private String id;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_locked")
    private boolean isLocked;

    @Column(name = "stock_status")
    private String stockStatus;

    @OneToOne
    @MapsId
    @JoinColumn(name = "transaction_id")
    private FinancialTransaction transaction;

    // Default constructor
    public FiscalPeriod() {
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public FinancialTransaction getTransaction() {
        return transaction;
    }

    public void setTransaction(FinancialTransaction transaction) {
        this.transaction = transaction;
    }
}
