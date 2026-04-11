package org.example.ivms.dto;

import java.time.LocalDate;

public class ProcurementShipmentDTO {
    private String poNumber;
    private Long vendorId;
    private String itemsText;
    private Integer totalQty;
    private String origin;
    private String destination;
    private String status;
    private String trackingId;
    private LocalDate estimatedArrival;

    public String getPoNumber() { return poNumber; }
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    public Long getVendorId() { return vendorId; }
    public void setVendorId(Long vendorId) { this.vendorId = vendorId; }

    public String getItemsText() { return itemsText; }
    public void setItemsText(String itemsText) { this.itemsText = itemsText; }

    public Integer getTotalQty() { return totalQty; }
    public void setTotalQty(Integer totalQty) { this.totalQty = totalQty; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTrackingId() { return trackingId; }
    public void setTrackingId(String trackingId) { this.trackingId = trackingId; }

    public LocalDate getEstimatedArrival() { return estimatedArrival; }
    public void setEstimatedArrival(LocalDate estimatedArrival) { this.estimatedArrival = estimatedArrival; }
}