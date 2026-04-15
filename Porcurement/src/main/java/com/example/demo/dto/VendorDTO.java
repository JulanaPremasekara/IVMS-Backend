package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorDTO {
    private Long id;
    private String name;
    private String status;         // "Active", "Inactive"
    private Double rating;         // 4.5
    private String paymentTerms;   // "Net 30"
    private String contractExpiry;
    private List<String> shippingMethods;
}
