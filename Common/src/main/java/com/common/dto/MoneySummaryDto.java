package com.common.dto;

import java.math.BigDecimal;

public record MoneySummaryDto(
        BigDecimal itemsSubtotal,
        BigDecimal tax,
        BigDecimal shipping,
        BigDecimal total
) {}