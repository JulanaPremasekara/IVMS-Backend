package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePurchaseResponseDto {
    private Long id;

    private Long vendorId;
    private String vendor;

    private String status;

    private LocalDate createdDate;
    private LocalDate expectedDeliveryDate;

    private String shippingMethod;
    private String internalNotes;

    private Double totalAmount;
    private String createdBy;

    private LocalDateTime voidedAt;

    private List<PurchaseOrderItemResponseDto> items;
}
