package com.common.dto;

import java.time.LocalDate;

public record SupplierDto(
        Long id,
        String name,
        boolean active,
        double reliabilityScore,
        String paymentTerms,
        LocalDate contractExpiryDate,
        String contractUrl
) {}
