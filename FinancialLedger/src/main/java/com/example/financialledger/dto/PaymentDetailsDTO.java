package com.example.financialledger.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentDetailsDTO(

        // From PAYMENT_HISTORY
        Long paymentId,
        BigDecimal amountPaid,
        LocalDateTime paymentDate,
        String paymentMethod,
        String referenceNumber,
        LocalDateTime dueDate,
        BigDecimal remainingBalance,

        // From FINANCIAL_TRANSACTION
        String id,
        String orderId,
        String partyName,
        BigDecimal totalAmount,
        String status
) {}