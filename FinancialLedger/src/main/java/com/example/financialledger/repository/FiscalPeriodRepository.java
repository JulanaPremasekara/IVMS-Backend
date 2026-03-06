package com.example.financialledger.repository;

import com.example.financialledger.model.FiscalPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FiscalPeriodRepository extends JpaRepository<FiscalPeriod, String> {
}
