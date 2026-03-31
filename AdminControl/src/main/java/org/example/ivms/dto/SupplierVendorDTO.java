package org.example.ivms.dto;

public class SupplierVendorDTO {
    private Long id;
    private String companyName;
    private String taxId;
    private String address;
    private String email;
    private String phone;
    private String contactPerson;
    private String paymentTerms;
    private String startDate;
    private String endDate;
    private String sku;
    private String unitPrice;
    private String status;
    private Double performanceScore;
    private Double avgLeadTime;
    private Integer delayProbability;
    private Double reliabilityRating;
    private Integer totalOrders;
    private String blacklistReason;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getPaymentTerms() { return paymentTerms; }
    public void setPaymentTerms(String paymentTerms) { this.paymentTerms = paymentTerms; }

    public String getStartDate() { return startDate; }
    public void setStartDate(String startDate) { this.startDate = startDate; }

    public String getEndDate() { return endDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getUnitPrice() { return unitPrice; }
    public void setUnitPrice(String unitPrice) { this.unitPrice = unitPrice; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }

    public Double getAvgLeadTime() { return avgLeadTime; }
    public void setAvgLeadTime(Double avgLeadTime) { this.avgLeadTime = avgLeadTime; }

    public Integer getDelayProbability() { return delayProbability; }
    public void setDelayProbability(Integer delayProbability) { this.delayProbability = delayProbability; }

    public Double getReliabilityRating() { return reliabilityRating; }
    public void setReliabilityRating(Double reliabilityRating) { this.reliabilityRating = reliabilityRating; }

    public Integer getTotalOrders() { return totalOrders; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }

    public String getBlacklistReason() { return blacklistReason; }
    public void setBlacklistReason(String blacklistReason) { this.blacklistReason = blacklistReason; }
}