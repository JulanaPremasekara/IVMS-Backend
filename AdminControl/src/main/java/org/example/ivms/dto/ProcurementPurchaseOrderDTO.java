package org.example.ivms.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProcurementPurchaseOrderDTO {
    private String poNumber;
    private String vendorName;
    private String status;
    private String createdDate;
    private String expectedDeliveryDate;
    private BigDecimal totalAmount;
    private String createdBy;
    private List<String> itemSkus;

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getExpectedDeliveryDate() { return expectedDeliveryDate; }
    public void setExpectedDeliveryDate(String expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<String> getItemSkus() { return itemSkus; }
    public void setItemSkus(List<String> itemSkus) { this.itemSkus = itemSkus; }
}