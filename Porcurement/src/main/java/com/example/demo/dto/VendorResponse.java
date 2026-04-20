package com.example.demo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorResponse {

    @NotBlank(message = "Company name is required")
    @Size(min = 2, max = 100, message = "Company name must be between 2 and 100 characters")
    private String companyName;

    @NotBlank(message = "Tax ID is required")
    @Pattern(regexp = "^[0-9]{9}$", message = "Tax ID must be exactly 9 digits")
    private String taxId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Contact person name is required")
    private String contactPerson;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number must be valid (10-15 digits)")
    private String phone;

    @NotBlank(message = "Payment terms are required")
    private String paymentTerms;

    @NotBlank(message = "Start date is required")
    private String startDate;

    @NotBlank(message = "End date is required")
    private String endDate;

    @NotBlank(message = "SKU is required")
    private String sku;

    @DecimalMin(value = "0.0", inclusive = false, message = "Unit price must be greater than zero")
    private String unitPrice;

    private String status;

}
