package com.example.demo.dto;

import java.math.BigDecimal;
import java.util.List;

public record PurchaseOrderResponse(

        String id,          // poNumber
        String vendor,      // vendor name
        String status,      // formatted status
        String date,        // formatted createdDate
        String delivery,    // formatted expectedDeliveryDate
        BigDecimal total,
        String creator,     // createdBy
        List<String> items  // item names
) {}
