package com.example.financialledger.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionDTO(

                String id, // optional (ignored on save)

                @JsonProperty("order") String orderId,

                String type,
                @JsonProperty("party") String partyName,
                BigDecimal amount,
                BigDecimal tax,
                BigDecimal total,
                String status,
                @JsonProperty("time") @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime createdAt,
                String category,
                String fiscalPeriod,
                boolean isLocked,
                String stockStatus) {
}