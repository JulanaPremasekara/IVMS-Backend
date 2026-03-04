package com.example.Supplier_Portal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendorRequest {
    private String companyName;
    private String taxId;
    private String address;
    private String contactPerson;
    private String email;
    private String phone;
    private String paymentTerms;
    private String startDate;
    private String endDate;
    private String sku;
    private String unitPrice;
    private String status;
}