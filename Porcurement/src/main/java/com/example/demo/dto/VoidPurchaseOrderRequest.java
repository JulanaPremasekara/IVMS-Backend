package com.example.demo.dto;

public class VoidPurchaseOrderRequest {
    private String note;
    private Boolean notifyVendor;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getNotifyVendor() {
        return notifyVendor;
    }

    public void setNotifyVendor(Boolean notifyVendor) {
        this.notifyVendor = notifyVendor;
    }
}
