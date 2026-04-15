package org.example.ivms.dto;

import java.time.LocalDate;
import java.util.List;

public class ProcurementIncomingShipmentDTO {
    private Long shipmentId;
    private String poNumber;
    private String trackingId;
    private String supplierName;
    private LocalDate expectedDate;
    private String status;
    private List<Object> items;

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }

    public LocalDate getExpectedDate() { return expectedDate; }
    public void setExpectedDate(LocalDate expectedDate) { this.expectedDate = expectedDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Object> getItems() { return items; }
    public void setItems(List<Object> items) { this.items = items; }
}